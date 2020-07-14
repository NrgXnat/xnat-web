/*
 * web: exportEndpointManager.js
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

/*!
 * Manage Export Endpoints
 */

console.log('exportEndpointManager.js');

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

    var exportEndpointManager, undefined, undef,
        rootUrl = XNAT.url.rootUrl,
        restUrl = XNAT.url.restUrl;

    XNAT.admin =
        getObject(XNAT.admin || {});

    XNAT.admin.exportEndpointManager = exportEndpointManager =
        getObject(XNAT.admin.exportEndpointManager || {});


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

    function endpointUrl(appended, cacheParam){
        appended = appended ? '/' + appended : '';
        return restUrl('/xapi/exportendpoint' + appended, '', cacheParam || false);
    }


    // keep track of used labels to help prevent label conflicts
    exportEndpointManager.labels = [];

    // get the list of Export Endpoints
    exportEndpointManager.getEndpoints = exportEndpointManager.getAll = function(callback){
        callback = isFunction(callback) ? callback : function(){};
        exportEndpointManager.labels = [];
        return XNAT.xhr.get({
            url: endpointUrl('list', true),
            dataType: 'json',
            success: function(data){
                exportEndpointManager.definitions = data;
                // refresh the 'usedAeTitlesAndPorts' array every time this function is called
                data.forEach(function(item){
					var itemObj = JSON.parse(item.contents);
                    exportEndpointManager.labels.push(itemObj.label);
                });
                callback.apply(this, arguments);
            }
        });
    };


    exportEndpointManager.getEndpoint = exportEndpointManager.getOne = function(label, callback){
        if (!label) return null;
        callback = isFunction(callback) ? callback : function(){};
        return XNAT.xhr.get({
            url: endpointUrl('get?label='+label, true),
            dataType: 'json',
            success: callback
        });
    };

    exportEndpointManager.get = function(label){
        if (!label) {
            return exportEndpointManager.getAll();
        }
        return exportEndpointManager.getOne(label);
    };

    // dialog to create/edit endpoint definition
   exportEndpointManager.dialog = function(endpointDefinition,newCommand){
        var _source,_editor;
         if (!newCommand) {
 		    var endpointDefinitionObj = JSON.parse(endpointDefinition.contents);
 	        var label = endpointDefinitionObj.label

 			label = label || {};

            var dialogButtons = {
                update: {
                    label: 'Save',
                    isDefault: true,
                    action: function(){
                        var editorContent = _editor.getValue().code;

                        var url = endpointUrl('/add?overwrite=true');

                        XNAT.xhr.post({
                            url: url,
                            contentType: 'text/plain',
                            data: editorContent,
                            success: function(){
                                exportEndpointManager.refreshTable();
                                xmodal.closeAll();
                                XNAT.ui.banner.top(2000, 'Export endpoint definition updated.', 'success');
                            },
                            fail: function(e){
                                errorHandler(e, 'Could Not Update', false);
                            }
                        });
                    }
                },
                close: { label: 'Cancel' }
            };


            _source = spawn ('textarea', endpointDefinition.contents);

            _editor = XNAT.app.codeEditor.init(_source, {
                language: 'json'
            });

            _editor.openEditor({
                title: 'Edit Export Definition For ' + label,
                classes: 'plugin-json',
                buttons: dialogButtons,
                height: 680,
                afterShow: function(dialog, obj){
                    obj.aceEditor.setReadOnly(false);
                    dialog.$modal.find('.body .inner').prepend(
                        spawn('div',[
                            spawn('p', 'Export Endpoint Label: '+label),
                        ])
                    );
                }
            });
        } else {
            _source = spawn('textarea', '{}');

            _editor = XNAT.app.codeEditor.init(_source, {
                language: 'json'
            });

            _editor.openEditor({
                title: 'Add New Export Definition',
                classes: 'plugin-json',
                buttons: {
                    create: {
                        label: 'Save Command',
                        isDefault: true,
                        action: function(){
                            var editorContent = _editor.getValue().code;

                            var url = endpointUrl('/add');

                            XNAT.xhr.post({
                                url: url,
                                contentType: 'text/plain',
                                data: editorContent,
                                success: function(obj){
                                    exportEndpointManager.refreshTable();
                                    xmodal.close(obj.$modal);
                                    XNAT.ui.banner.top(2000, 'Export endpoint definition created.', 'success');
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

    // create table for DICOM SCP receivers
    exportEndpointManager.table = function(container, callback){

        // initialize the table - we'll add to it below
        var exportEndpointTable = XNAT.table({
            className: 'export-endpoint xnat-table',
            style: {
                width: '100%',
                marginTop: '15px',
                marginBottom: '15px'
            }
        });

        // add table header row
        exportEndpointTable.tr()
                .th({ addClass: 'left', html: '<b>Label</b>' })
                .th('<b>Export Handler Annotation</b>')
                .th('<b>Enabled</b>')
                .th('<b>Actions</b>');

        // TODO: move event listeners to parent elements - events will bubble up
        // ^-- this will reduce the number of event listeners
        function enabledCheckbox(item){
            var enabled = !!item.enabled;
            var ckbox = spawn('input.export-endpoint-enabled', {
                type: 'checkbox',
                checked: enabled,
                value: enabled,
                data: { label: item.label},
                onchange: function(){
                    // save the status when clicked
                    var checkbox = this;
                    enabled = checkbox.checked;
                    XNAT.xhr.put({
                        url: endpointUrl(item.label + '/enabled/' + enabled),
                        success: function(){
                            var status = (enabled ? ' enabled' : ' disabled');
                            checkbox.value = enabled;
                            XNAT.ui.banner.top(1000, '<b>' + item.label + '</b> ' + status, 'success');
                            console.log(item.label + status)
                        }
                    });
                }
            });
            return spawn('div.center', [
                ['label.switchbox|title=' + item.label, [
                    ckbox,
                    ['span.switchbox-outer', [['span.switchbox-inner']]]
                ]]
            ]);
        }

        function editLink(item, text){
			var itemObj = JSON.parse(item.contents);
            return spawn('a.link|href=#!', {
                onclick: function(e){
                    e.preventDefault();
                    if (itemObj && itemObj.label) {
                        exportEndpointManager.getEndpoint(itemObj.label, function(data){
                            exportEndpointManager.dialog(data, false);
                        });
                    }
                    else {
                        exportEndpointManager.dialog({}, false);
                    }
                }
            }, [['b', text]]);
        }

        function editButton(item){
			var itemObj = JSON.parse(item.contents);
            return spawn('button.btn.sm.edit', {
                onclick: function(e){
                    e.preventDefault();
                    if (itemObj && itemObj.label) {
                        exportEndpointManager.getEndpoint(itemObj.label, function(data){
                            exportEndpointManager.dialog(data, false);
                        });
                    }
                    else {
                        exportEndpointManager.dialog({}, false);
                    }
                }
            }, 'Edit');
        }

        function deleteButton(item){
			var itemObj = JSON.parse(item.contents);
            return spawn('button.btn.sm.delete', {
                onclick: function(){
                    XNAT.dialog.confirm({
                        // height: 220,
                        title: 'Delete endpoint?',
                        scroll: false,
                        content: '' +
                        "<p>Are you sure you'd like to delete the '<b>" + itemObj.label + "</b>' export endpoint definition?</p>" +
                        '<p><b><i class="fa fa-exclamation-circle"></i> This action cannot be undone.</b></p>' +
                        "",
                        okLabel: "Delete",
                        okAction: function(){
                            console.log('delete label ' + itemObj.label);
                            XNAT.xhr.delete({
                                url: endpointUrl(itemObj.label),
                                success: function(){
                                    console.log('"' + itemObj.label + '" deleted');
                                    XNAT.ui.banner.top(1000, '<b>"' + itemObj.label + '"</b> deleted.', 'success');
                                    refreshTable();
                                }
                            });
                        }
                    })
                }
            }, 'Delete');
        }

        exportEndpointManager.getAll().done(function(data){
            data.forEach(function(item){
				var itemObj = JSON.parse(item.contents);
                var identifierLabel = itemObj.label || 'exportEndpointObjectLabel';
                identifierLabel += (identifierLabel === 'exportEndpointObjectLabel') ? ' (Default)' : '';
                exportEndpointTable.tr({ title: itemObj.label, data: { label: itemObj.label, handler: itemObj['export-handler'] } })
                        .td([editLink(item, itemObj.label)]).addClass('label')
                        .td([['div.mono.center', itemObj['export-handler']]]).addClass('exportHandler')
                        .td([enabledCheckbox(item)]).addClass('status')
                        .td([['div.center', [editButton(item), spacer(10), deleteButton(item)]]]);
            });
            if (container) {
                $$(container).append(exportEndpointTable.table);
            }

            if (isFunction(callback)) {
                callback(exportEndpointTable.table);
            }

        });

        exportEndpointManager.$table = $(exportEndpointTable.table);

        return exportEndpointTable.table;
    };

    exportEndpointManager.init = function(container){

        exportEndpointManager.getEndpoints().done(function(data){

            exportEndpointManager.labels = data;

            var $manager = $$(container || 'div#export-endpoint-manager');

            exportEndpointManager.$container = $manager;

            $manager.append(exportEndpointManager.table());

            var newEndpoint = spawn('button.new-export-endpoint.btn.btn-sm.submit', {
                html: 'Add New Export Endpoint',
                onclick: function(){
                    exportEndpointManager.dialog(null, true);
                }
            });

            // add  'add new' buttons at the bottom
            $manager.append(spawn('div', [
                newEndpoint,
                ['div.clear.clearfix']
            ]));

            return {
                element: $manager[0],
                spawned: $manager[0],
                get: function(){
                    return $manager[0]
                }
            };

        });
    };


   exportEndpointManager.refresh = exportEndpointManager.refreshTable = function(){
        exportEndpointManager.$table.remove();
        exportEndpointManager.table(null, function(table){
            exportEndpointManager.$container.prepend(table);
        });
    };

    exportEndpointManager.init();

    return XNAT.admin.exportEndpointManager = exportEndpointManager;

}));
