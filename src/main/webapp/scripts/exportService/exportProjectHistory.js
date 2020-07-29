/*
 * web: exportProjectHistory.js
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2020, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

/*!
 * Manage Project Export Endpoints
 */

console.log('exportProjectHistory.js');

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

    var exportProjectHistoryManager, undefined, undef,
        rootUrl = XNAT.url.rootUrl,
        restUrl = XNAT.url.restUrl;

    XNAT.exportProjectHistory =
        getObject(XNAT.exportProjectHistory || {});

    XNAT.exportProjectHistory.exportProjectHistoryManager = exportProjectHistoryManager =
        getObject(XNAT.exportProjectHistory.exportProjectHistoryManager || {});


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

//	function getProjectId(){
//		return "TEST2";
//		//return XNAT.data.context.projectID;
//	}

	function convertToDate(timestamp) {
		var dateObj = new Date(timestamp);
	    //return dateObj.toUTCString();
	    return dateObj.toLocaleString();
	}

	function displayStatus(item) {
		var status = item['succeeded'];
		return (status === true ? "Completed" : "In Progress/Failed");
	}

	function getLogFileName(trackingId) {
		var toFileName = trackingId.replace(/\//g, "_").replace(/:/g, "_").replace(/ /g, "_") + ".log";
		return toFileName;
	}

	function getFileUrl(logFileName) {
		var projectId = getProjectId();
        var url = '/data/projects/' + projectId + "/resources/export_logs/files/" + logFileName;
        return url;
	}

    function projectEndpointUrl(appended, cacheParam){
        appended = appended ? '/' + appended : '';
        var projectId = getProjectId();
        var url = '/xapi/export/history/projects/' + projectId + appended;
        return restUrl(url, {format: 'json'}, cacheParam || false);
    }




    // keep track of used labels to help prevent label conflicts
    exportProjectHistoryManager.trackingIds = [];

    // get the list of Export Endpoints
    exportProjectHistoryManager.getHistories = exportProjectHistoryManager.getAll = function(callback){
        callback = isFunction(callback) ? callback : function(){};
        exportProjectHistoryManager.trackingIds = [];
        return XNAT.xhr.get({
            url: projectEndpointUrl('',true),
            dataType: 'json',
            success: function(data){
                exportProjectHistoryManager.definitions = data;
                // refresh the 'usedAeTitlesAndPorts' array every time this function is called
                data.forEach(function(item){
                    exportProjectHistoryManager.trackingIds.push(item.key);
                });
                callback.apply(this, arguments);
            }
        });
    };

   exportProjectHistoryManager.getLogFile  = function(trackingId){
        if (!trackingId) return null;
        var logFileName  = getLogFileName(trackingId);
        var fileUrl = getFileUrl(logFileName);
        return fileUrl;
    };



    exportProjectHistoryManager.getHistory = exportProjectHistoryManager.getOne = function(trackingId, callback){
        if (!trackingId) return null;
        callback = isFunction(callback) ? callback : function(){};
        return XNAT.xhr.get({
            url: projectEndpointUrl('tracking/'+trackingId, true),
            dataType: 'json',
            success: callback
        });
    };

    exportProjectHistoryManager.get = function(trackingId){
        if (!trackingId) {
            return exportProjectHistoryManager.getAll();
        }
        return exportProjectHistoryManager.getOne(trackingId);
    };

    // dialog to create/edit endpoint definition
   exportProjectHistoryManager.dialog = function(payload){
        var _source,_editor;
            _source = spawn('textarea', payload);

            _editor = XNAT.app.codeEditor.init(_source, {
                language: 'json'
            });

            _editor.openEditor({
                title: 'View History',
                classes: 'plugin-json',
                buttons: {
                    close: {
                        label: 'Close'
                    }
                }
            });
    };

    exportProjectHistoryManager.table = function(container, callback){

        // initialize the table - we'll add to it below
        var exportProjectHistoryTable = XNAT.table({
            className: 'project-export-history xnat-table',
            style: {
                width: '100%',
                marginTop: '15px',
                marginBottom: '15px',
                'overflow-y': 'auto'
            }
        });

        // add table header row
        exportProjectHistoryTable.tr()
                .th({ addClass: 'left', html: '<b>Start</b>' })
                .th('<b>Message</b>')
                .th('<b>End</b>')
                .th('<b>Status</b>')
                .th('<b>Actions</b>');


        function viewPayload(item, text){
			var payload = JSON.parse(item.payload);
            return spawn('button.btn.sm.edit', {
                onclick: function(e){
                    e.preventDefault();
                    if (item && item.key) {
                       exportProjectHistoryManager.dialog(item.payload, false);
                    }
                }
            }, 'Details');
        }

        function getLogFileButton(item){
			var key = item.key;
            var logFileUrl = exportProjectHistoryManager.getLogFile(item.key);
			return spawn('a.link|href=#!',{
						  onclick: function(e) {
								 e.preventDefault();
								 window.open(logFileUrl,'_blank'); }
						   },[['b', 'Log File']]);
        }


        exportProjectHistoryManager.getAll().done(function(data){
            data.forEach(function(item){
                var key = item.key || 'exportProjectHistoryKey';
                key += (key === 'exportProjectHistoryKey') ? ' (Default)' : '';
                exportProjectHistoryTable.tr({ title: item.key, data: { key: item.key, payload: item['payload'] } })
                        .td([convertToDate(item.created)]).addClass('created')
                        .td([['div.mono.center', item['finalMessage']]]).addClass('finalMessage')
                        .td([['div.mono.center', convertToDate(item['timestamp'])]]).addClass('timestamp')
                        .td([['div.mono.center', displayStatus(item)]]).addClass('status')
                        .td([['div.center', [viewPayload(item), spacer(10), getLogFileButton(item)]]]);
            });
            if (container) {
                $$(container).append(exportProjectHistoryTable.table);
            }

            if (isFunction(callback)) {
                callback(exportProjectHistoryTable.table);
            }

        });

        exportProjectHistoryManager.$table = $(exportProjectHistoryTable.table);

        return exportProjectHistoryTable.table;
    };

    exportProjectHistoryManager.init = function(container){

        exportProjectHistoryManager.getHistories().done(function(data){

           // exportProjectHistoryManager.trackingIds = data;

            var $manager = $$(container || 'div#export-history-container');

            exportProjectHistoryManager.$container = $manager;

            $manager.append(exportProjectHistoryManager.table());


            return {
                element: $manager[0],
                spawned: $manager[0],
                get: function(){
                    return $manager[0]
                }
            };

        });
    };


   exportProjectHistoryManager.refresh = exportProjectHistoryManager.refreshTable = function(){
        exportProjectHistoryManager.$table.remove();
        exportProjectHistoryManager.table(null, function(table){
            exportProjectHistoryManager.$container.prepend(table);
        });
    };

    exportProjectHistoryManager.init();

    return XNAT.exportProjectHistory.exportProjectHistoryManager = exportProjectHistoryManager;

}));
