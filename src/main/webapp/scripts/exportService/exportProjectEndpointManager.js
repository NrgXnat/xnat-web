/*
 * web: exportProjectEndpointManager.js
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

/*!
 * Manage Project Export Endpoints
 */

console.log('exportProjectEndpointManager.js');

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

    XNAT.projectOwner =
        getObject(XNAT.projectOwner || {});

    XNAT.projectOwner.exportEndpointManager = exportEndpointManager =
        getObject(XNAT.projectOwner.exportEndpointManager || {});


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

     function getUrlParams(){
	        var paramObj = {};

	        // get the querystring param, redacting the '?', then convert to an array separating on '&'
	        var urlParams = window.location.search.substr(1,window.location.search.length);
	        urlParams = urlParams.split('&');

	        urlParams.forEach(function(param){
	            // iterate over every key=value pair, and add to the param object
	            param = param.split('=');
	            paramObj[param[0]] = param[1];
	        });

	        return paramObj;
	    }

	function getProjectId(){
	        if (XNAT.data.context.projectID.length > 0) return XNAT.data.context.projectID;
	        return getUrlParams().id;
    }


    function siteEndpointUrl(appended, cacheParam){
        appended = appended ? '/' + appended : '';
        return restUrl('/xapi/exportendpoint' + appended, {format:'json'}, cacheParam || false);
    }

    function projectEndpointUrl(appended, format, cacheParam){
        appended = appended ? '/' + appended : '';
        return restUrl('/xapi/export/endpoint' +   appended, format, cacheParam || false);
    }


    // keep track of used labels to help prevent label conflicts
    exportEndpointManager.projectlabels = [];
    exportEndpointManager.sitelabels = [];
    exportEndpointManager.sitedefinitions = [];
    exportEndpointManager.siteNotInProjectdefinitions = [];
    exportEndpointManager.projectdefinitions = [];

    // get the list of Site wide Export Endpoints
    exportEndpointManager.setEndpoints = exportEndpointManager.setAll = function(callback){
        callback = isFunction(callback) ? callback : function(){};
		XNAT.ui.banner.top(2000, 'Loading data....', 'please wait');

        exportEndpointManager.sitedefinitions = [];
        exportEndpointManager.siteNotInProjectdefinitions = [];
        exportEndpointManager.projectdefinitions = [];
        exportEndpointManager.getSiteAll();
        exportEndpointManager.getProjectAll();
		xmodal.closeAll();

		for (var i = 0; i < exportEndpointManager.sitedefinitions.length; i++) {
		  var isInProject = false;
		  for (var j = 0; j < exportEndpointManager.projectdefinitions.length; j++) {
			  if (exportEndpointManager.projectdefinitions[j].path === exportEndpointManager.sitedefinitions[i].path) {
			    isInProject = true;
			  }
		  }
		  if (!isInProject) {
		    exportEndpointManager.siteNotInProjectdefinitions.push(exportEndpointManager.sitedefinitions[i]);
		  }
		}
        exportEndpointManager.projectdefinitions = exportEndpointManager.projectdefinitions.concat(exportEndpointManager.siteNotInProjectdefinitions);
        return;
    };



    // get the list of Site wide Export Endpoints
    exportEndpointManager.getSiteEndpoints = exportEndpointManager.getSiteAll = function(callback){
        callback = isFunction(callback) ? callback : function(){};
        return XNAT.xhr.get({
            url: siteEndpointUrl('list', true),
            dataType: 'json',
            async: false,
            success: function(data){
                exportEndpointManager.sitedefinitions = [];
                data.forEach(function(item){
					if(item.status === 'enabled') {
						var siteItem = item;
						siteItem.isProject = false;
						exportEndpointManager.sitedefinitions.push(siteItem);
					}
                });
                callback.apply(this, arguments);
            }
        });
    };

    // get the list of Project Export Endpoints
    exportEndpointManager.getProjectEndpoints = exportEndpointManager.getProjectAll = function(callback){
        callback = isFunction(callback) ? callback : function(){};
        return XNAT.xhr.get({
            url: projectEndpointUrl('list/' + getProjectId(), {},true),
            dataType: 'json',
            async: false,
            success: function(data){
                exportEndpointManager.projectdefinitions = [];
                data.forEach(function(item){
					var projectItem = item;
					projectItem.isProject = true;
                    exportEndpointManager.projectdefinitions.push(projectItem);
                });
                callback.apply(this, arguments);
            }
        });
    };


    exportEndpointManager.getEndpoint = exportEndpointManager.getOne = function(label, callback){
        if (!label) return null;
        callback = isFunction(callback) ? callback : function(){};
        return XNAT.xhr.get({
            url: projectEndpointUrl('get/'+getProjectId()+'?label='+label, true),
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

                        var url = projectEndpointUrl('projects/' + getProjectId());

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
			var itemObj = JSON.parse(item.contents);
            var enabled = item.isProject && item.status === 'enabled' ;
            var ckbox = spawn('input.export-endpoint-enabled', {
                type: 'checkbox',
                checked: enabled,
                value: enabled,
                onchange: function(){
                    // save the endpoint definition when clicked
                    var checkbox = this;
                    enabled = checkbox.checked;
                    if (!item.isProject) {
						//Add to project
	                    XNAT.xhr.post({
	                        url: projectEndpointUrl('projects/' + getProjectId(),'', false),
	                        data: item.contents,
	                        contentType: 'text/plain',
	                        success: function(){
	                            var status = (enabled ? ' enabled' : ' disabled');
	                            checkbox.value = enabled;
	                            XNAT.ui.banner.top(1000, '<b>' + itemObj.label + '</b> ' + status, 'success');
	                        },
	                        failure: function(e) {
								errorHandler(e);
							}
	                    });
					}else {
						//Already in project, set as per preference
	                    XNAT.xhr.post({
	                        url: projectEndpointUrl('enable/' + getProjectId() + '?label='+itemObj.label + '&enabled=' +enabled),
	                        success: function(){
	                            var status = (enabled ? ' enabled' : ' disabled');
	                            checkbox.value = enabled;
	                            XNAT.ui.banner.top(1000, '<b>' + itemObj.label + '</b> ' + status, 'success');
	                        }
	                    });
					}
					exportEndpointManager.refreshTable();

                }
            });
            return spawn('div.center', [
                ['label.switchbox|title=' + itemObj.label, [
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
			var disable = false;
			if (item.status === 'disabled') {
				disable = true;
			}
				return spawn('button.btn.sm.edit', {
					disabled:disable,
					onclick: function(e){
						e.preventDefault();
						if (itemObj &&   itemObj.label) {
							exportEndpointManager.getEndpoint(itemObj.label, function(data){
								exportEndpointManager.dialog(data, false);
							});
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
                                url: projectEndpointUrl(itemObj.label),
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
		for (var k=0; k < exportEndpointManager.projectdefinitions.length; k++) {
			    var item = exportEndpointManager.projectdefinitions[k];
				var itemObj = JSON.parse(item.contents);
                var identifierLabel = itemObj.label || 'exportEndpointObjectLabel';
                identifierLabel += (identifierLabel === 'exportEndpointObjectLabel') ? ' (Default)' : '';
                exportEndpointTable.tr({ title: itemObj.label, data: { label: itemObj.label, handler: itemObj['export-handler'] } })
                        //.td([editLink(item, itemObj.label)]).addClass('label')
                        .td([itemObj.label]).addClass('label')
                        .td([['div.mono.center', itemObj['export-handler']]]).addClass('exportHandler')
                        .td([enabledCheckbox(item)]).addClass('status')
                        //.td([['div.center', [editButton(item), spacer(10), deleteButton(item)]]]);
                        .td([['div.center', [editButton(item)]]]);

            if (container) {
                $$(container).append(exportEndpointTable.table);
            }

            if (isFunction(callback)) {
                callback(exportEndpointTable.table);
            }


		}

        exportEndpointManager.$table = $(exportEndpointTable.table);

        return exportEndpointTable.table;
    };

    exportEndpointManager.init = function(container){

            var $manager = $$(container || 'div#proj-export-config-list-container');

            exportEndpointManager.$container = $manager;

        	exportEndpointManager.setEndpoints();
        	$manager.append(exportEndpointManager.table());

				return {
					element: $manager[0],
					spawned: $manager[0],
					get: function(){
						return $manager[0]
					}
				};

    };


   exportEndpointManager.refresh = exportEndpointManager.refreshTable = function(){
        exportEndpointManager.setEndpoints();
		console.log("Crossed this");
        if (typeof exportEndpointManager.$table != 'undefined') {
        	exportEndpointManager.$table.remove();
		}
        exportEndpointManager.table(null, function(table){
            exportEndpointManager.$container.prepend(table);
        });
    };

    exportEndpointManager.init();

    return XNAT.projectOwner.exportEndpointManager = exportEndpointManager;

}));
