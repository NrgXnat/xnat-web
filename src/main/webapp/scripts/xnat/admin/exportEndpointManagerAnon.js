/*
 * web: exportEndpointManagerAnon.js
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

/*!
 * Manage Export Endpoint Anonymizations
 */

console.log('exportEndpointManagerAnon.js');

var XNAT = getObject(XNAT || {});

(function(factory){
    if (typeof define === 'function' && define.amd) {
        define(factory);
    }
    else if (typeof exports === 'object') {
        module.exports = factory();
    }
    else {
        return factory();
    }
}(function(){

    var exportEndpointManagerAnon, undefined, undef,
        rootUrl = XNAT.url.rootUrl,
        restUrl = XNAT.url.restUrl;

    XNAT.admin =
        getObject(XNAT.admin || {});

    XNAT.admin.exportEndpointManagerAnon = exportEndpointManagerAnon =
        getObject(XNAT.admin.exportEndpointManagerAnon || {});


    function spacer(width){
        return spawn('i.spacer', {
            style: {
                display: 'inline-block',
                width: width + 'px'
            }
        })
    }

   function errorHandler(e, title, closeAll){
        console.log(e);
        title = (title) ? 'Error Found: '+ title : 'Error';
        closeAll = (closeAll === undefined) ? true : closeAll;
        var errormsg = (e.statusText) ? '<p><strong>Error ' + e.status + ': '+ e.statusText+'</strong></p><p>' + e.responseText + '</p>' : e;
        XNAT.dialog.open({
            width: 450,
            title: title,
            content: errormsg,
            buttons: [
                {
                    label: 'OK',
                    isDefault: true,
                    close: true,
                    action: function(){
                        if (closeAll) {
                            xmodal.closeAll();

                        }
                    }
                }
            ]
        });
    }

    function anonEndpointUrl(appended, cacheParam){
        appended = appended ? '/' + appended : '';
        return restUrl('/data/config/export-service-anon' + appended, '{format:json}', cacheParam || false, true);
    }



    // keep track of used labels to help prevent label conflicts
    exportEndpointManagerAnon.paths = [];

    // get the list of Export Endpoints
    exportEndpointManagerAnon.getAnonymizations = exportEndpointManagerAnon.getAll = function(callback){
        callback = isFunction(callback) ? callback : function(){};
        exportEndpointManagerAnon.paths = [];
        return XNAT.xhr.get({
            url: anonEndpointUrl(null, true),
            success: function(data){
                exportEndpointManagerAnon.definitions = data;
                data.ResultSet.Result.forEach(function(item){
                    exportEndpointManagerAnon.paths.push(item.path);
                });
                callback.apply(this, arguments);
            },
            fail:function(e){  console.log("Failed to get export anonymizations");}
        });
    };


    exportEndpointManagerAnon.getAnon = exportEndpointManagerAnon.getOne = function(path, callback){
        if (!path) return null;
        callback = isFunction(callback) ? callback : function(){};
        return XNAT.xhr.get({
            url: anonEndpointUrl(path, true),
            success: callback,
            fail: function(e){errorHandler(e,"Failed to get export anonymizations",false);}
        });
    };

    exportEndpointManagerAnon.get = function(path){
        if (!path) {
            return exportEndpointManagerAnon.getAll();
        }
        return exportEndpointManagerAnon.getOne(path);
    };

    // dialog to create/edit endpoint anonymization
   exportEndpointManagerAnon.dialog = function(endpointAnon,newCommand){
        var _source,_editor, _path;
         if (!newCommand) {
			var endpointAnonObj = endpointAnon.ResultSet.Result[0];
 	        var path = endpointAnonObj.path;

 			path = path || {};

            var dialogButtons = {
                update: {
                    label: 'Save',
                    isDefault: true,
                    action: function(){
                        var editorContent = _editor.getValue().code;

                        var url = anonEndpointUrl(path);

                        XNAT.xhr.put({
                            url: url,
                            contentType: 'text/plain',
                            data: editorContent,
                            success: function(){
                                exportEndpointManagerAnon.refreshTable();
                                xmodal.closeAll();
                                XNAT.ui.banner.top(2000, 'Export anonymization script updated.', 'success');
                            },
                            fail: function(e){
                                errorHandler(e, 'Could Not Update', false);
                            }
                        });
                    }
                },
                close: { label: 'Cancel' }
            };

            _source = spawn ('textarea', endpointAnonObj.contents);
			_editor = XNAT.app.codeEditor.init(_source, {
                language: 'text'
            });

            _editor.openEditor({
                title: 'Edit Export Anonymization for ' + path,
                classes: 'plugin-text',
                buttons: dialogButtons,
                height: 680,
                afterShow: function(dialog, obj){
                    obj.aceEditor.setReadOnly(false);
                    dialog.$modal.find('.body .inner').prepend(
                        spawn('div',[
                            spawn('p', 'Export Anonymization Label: '+path),
                        ])
                    );
                }
            });


        } else {
            _source = spawn('textarea', '');
            _editor = XNAT.app.codeEditor.init(_source, {
	                    language: 'text'
	         });



	                _editor.openEditor({
	                    title: 'Add New Export Anonymization',
	                    classes: 'plugin-text',
						afterShow: function(dialog, obj){
							dialog.$modal.find('.body .inner').prepend(
								spawn('div',  [
											spawn('label.element-label', 'Path for export anonymization:'),
											spawn('div.element-wrapper', [
												spawn('label', [
													spawn('input', { type: 'text',id:'path', name: 'path', value: ''})
												])
											])
										])
							);
						},
	                    buttons: {
	                        create: {
	                            label: 'Save',
	                            isDefault: true,
	                            action: function(){
	                                var editorContent = _editor.getValue().code;
									var pathElt = $('input#path');
									var pathVal = pathElt.val();
									if (pathVal === "") {
											XNAT.dialog.open({
												width: 450,
												title: 'Error',
												content: 'Please enter value for path to save this anon script to',
												buttons: [
													{
														label: 'Close',
														isDefault: true,
														close: true
													}
												]
											});
									}
	                                var url = anonEndpointUrl(pathVal+'?unversioned=true');

	                                XNAT.xhr.put({
	                                    url: url,
	                                    contentType: 'text/plain',
	                                    data: editorContent,
	                                    success: function(obj){
	                                        exportEndpointManagerAnon.refreshTable();
	                                        xmodal.close(obj.$modal);
	                                        XNAT.ui.banner.top(2000, 'Export endpoint anonymization created.', 'success');
	                                    },
	                                    fail: function(e){
	                                        errorHandler(e, 'Could Not Save', false);
	                                    }
	                                });
	                            }
	                        },
	                        close: {
	                            label: 'Cancel'
	                        }
	                    }
	                });

        }
    };

    // create table for Export Anon scipts
    exportEndpointManagerAnon.table = function(container, callback){

        // initialize the table - we'll add to it below
        var exportEndpointAnonTable = XNAT.table({
            className: 'export-endpoint-anon xnat-table',
            style: {
                width: '100%',
                marginTop: '15px',
                marginBottom: '15px'
            }
        });

        // add table header row
        exportEndpointAnonTable.tr()
                .th({ addClass: 'left', html: '<b>Label</b>' })
                .th('<b>Enabled</b>')
                .th('<b>Actions</b>');

        // TODO: move event listeners to parent elements - events will bubble up
        // ^-- this will reduce the number of event listeners
        function enabledCheckbox(item){
            var enabled = item.status === "enabled";
            var ckbox = spawn('input.export-endpoint-anon-enabled', {
                type: 'checkbox',
                checked: enabled,
                value: enabled,
                data: { path: item.path},
                onchange: function(){
                    // save the status when clicked
                    var checkbox = this;
                    enabled = checkbox.checked;
                    var enabledTxt =enabled?'enabled':'disabled'
                    XNAT.xhr.put({
                        url: anonEndpointUrl(item.path + '?status=' + enabledTxt),
                        success: function(){
                            var status = (enabled ? ' enabled' : ' disabled');
                            checkbox.value = enabled;
                            XNAT.ui.banner.top(1000, '<b>' + item.path + '</b> ' + status, 'success');
                            console.log(item.path + status)
                        }
                    });
                }
            });
            return spawn('div.center', [
                ['label.switchbox|title=' + item.path, [
                    ckbox,
                    ['span.switchbox-outer', [['span.switchbox-inner']]]
                ]]
            ]);
        }

        function editLink(item, text){
            return spawn('a.link|href=#!', {
                onclick: function(e){
                    e.preventDefault();
                    if (item) {
                        exportEndpointManagerAnon.getAnon(item.path, function(data){
                            exportEndpointManagerAnon.dialog(data, false);
                        });
                    }
                    else {
                        exportEndpointManager.dialog('', false);
                    }
                }
            }, [['b', text]]);
        }

        function editButton(item){
            return spawn('button.btn.sm.edit', {
                onclick: function(e){
                    e.preventDefault();
                    if (item) {
                        exportEndpointManagerAnon.getAnon(item.path, function(data){
                            exportEndpointManagerAnon.dialog(data, false);
                        });
                    }
                    else {
                        exportEndpointManagerAnon.dialog('', false);
                    }
                }
            }, 'Edit');
        }

/*        function deleteButton(item){
            return spawn('button.btn.sm.delete', {
                onclick: function(){
                    XNAT.dialog.confirm({
                        // height: 220,
                        title: 'Delete export anonymization?',
                        scroll: false,
                        content: '' +
                        "<p>Are you sure you'd like to delete the '<b>" + item.path + "</b>' export anonymization?</p>" +
                        '<p><b><i class="fa fa-exclamation-circle"></i> This action cannot be undone.</b></p>' +
                        "",
                        okLabel: "Delete",
                        okAction: function(){
                            console.log('delete label ' + item.path);
                            XNAT.xhr.delete({
                                url: anonEndpointUrl(item.path),
                                success: function(){
                                    console.log('"' + item.path + '" deleted');
                                    XNAT.ui.banner.top(1000, '<b>"' + item.path + '"</b> deleted.', 'success');
                                    refreshTable();
                                }
                            });
                        }
                    })
                }
            }, 'Delete');
        } */

        exportEndpointManagerAnon.getAll().done(function(data){
            data.ResultSet.Result.forEach(function(item){
                var identifierLabel = item.path || 'exportEndpointAnonObjectLabel';
                identifierLabel += (identifierLabel === 'exportEndpointAnonObjectLabel') ? ' (Default)' : '';
                exportEndpointAnonTable.tr({ title: item.path, data: { path: item.path} })
                        .td([item.path]).addClass('path')
                        .td([enabledCheckbox(item)]).addClass('status')
                       // .td([['div.center', [editButton(item), spacer(10), deleteButton(item)]]]);
                        .td([['div.center', [editButton(item)]]]);
            });
            if (container) {
                $$(container).append(exportEndpointAnonTable.table);
            }

            if (isFunction(callback)) {
                callback(exportEndpointAnonTable.table);
            }

        });

        exportEndpointManagerAnon.$table = $(exportEndpointAnonTable.table);

        return exportEndpointAnonTable.table;
    };

    exportEndpointManagerAnon.init = function(container){
            var $manager = $$(container || 'div#export-endpoint-anon-manager');

            exportEndpointManagerAnon.$container = $manager;

            var newEndpoint = spawn('button.new-export-endpoint-anon.btn.btn-sm.submit', {
                html: 'Add New Export Anonymization',
                onclick: function(){
                    exportEndpointManagerAnon.dialog(null, true);
                }
            });

            // add  'add new' buttons at the bottom
            $manager.append(spawn('div', [
                newEndpoint,
                ['div.clear.clearfix']
            ]));

        exportEndpointManagerAnon.getAnonymizations().done(function(data){

            exportEndpointManagerAnon.paths = data;

            $manager.append(exportEndpointManagerAnon.table());

            return {
                element: $manager[0],
                spawned: $manager[0],
                get: function(){
                    return $manager[0]
                }
            };

        });


    };


   exportEndpointManagerAnon.refresh = exportEndpointManagerAnon.refreshTable = function(){
        if (typeof exportEndpointManagerAnon.$table != "undefined") {
			exportEndpointManagerAnon.$table.remove();
		}
        exportEndpointManagerAnon.table(null, function(table){
            exportEndpointManagerAnon.$container.prepend(table);
        });
    };

    exportEndpointManagerAnon.init();

    return XNAT.admin.exportEndpointManagerAnon = exportEndpointManagerAnon;

}));
