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

      var exporter,
        rootUrl = XNAT.url.rootUrl,
        csrfUrl = XNAT.url.csrfUrl,
        projectId = XNAT.data.context.projectID,
        exportMenuItems;

    	XNAT.app = getObject(XNAT.app || {});

        XNAT.app.exportService = getObject(XNAT.app.exportService || {});

        XNAT.app.exportService.exporter = exporter = getObject(XNAT.app.exportService.exporter || {});

    function errorHandler(e){
        var details = e.responseText ? spawn('p',[e.responseText]) : '';
        console.log(e);
        xmodal.alert({
            title: 'Error',
            content: '<p><strong>Error ' + e.status + ': '+ e.statusText+'</strong></p>' + details.html,
            okAction: function () {
                xmodal.closeAll();
            }
        });
    };


  exporter.open = window.openExportDialog = function(exportEndpointJsonStr){
	var exportEndpoint = JSON.parse(exportEndpointJsonStr);
	var label = exportEndpoint.label;
	var url = rootUrl("/scripts/exportService/"+ label  + "/" + label + ".js");
	$.getScript( url )
	  .done(function( script, textStatus ) {
	    console.log( "Loaded JS file for " + label );
	    var objName = label.replace(/-/g,'') + 'Exporter';
		var obj = window[objName];
		obj.openLaunchDialog(exportEndpoint);
	  })
	  .fail(function( jqxhr, settings, exception ) {
	    $( "div.log" ).text( "Triggered ajaxError handler." );
	});
  }


  exporter.exportMenuItems = exportMenuItems = [
	        {
	            text: 'Export To',
	            url: '#run',
	            submenu: {
	                id: 'exportSubmenuItems',
	                itemdata: [
	                ]
	            }
	        }
	    ];



    exporter.addYUIMenuItem = function(exportConfig){
        if (exportConfig.enabled) {
			var handlerObj = JSON.parse(exportConfig.contents);
            var label = exportConfig.path ;
            exportMenuItems[0].submenu.itemdata.push({
                text: label,
                url: 'javascript:openExportDialog('+ JSON.stringify(exportConfig.contents) +')',
                classname: 'enabled wrapped' // injects a custom classname onto the surrounding li element.
            });
        }
    };

    exporter.createYUIMenu = function(target){
        target = target || 'actionsMenu';
        var exportMenu = new YAHOO.widget.Menu('exportMenu', { autosubmenudisplay:true, scrollincrement:5, position:'static' });
        exportMenu.addItems(exportMenuItems);
        if (exportMenuItems[0].submenu.itemdata.length > 0) {
            exportMenu.render(target);
        }
    };


  exporter.init = function() {
           if (!projectId) {
               return;
           }
           XNAT.xhr.getJSON({
               url: rootUrl('/xapi/export/endpoint/list/' + projectId),
               success: function (data) {
                   var availableExportHandlers = data;
                   if (!availableExportHandlers.length) {
                       return false;
                   } else {
					   var isAnyEnabled = false;
                       availableExportHandlers.forEach(function (exportHandler) {
                           if (exportHandler.status === 'enabled') {
                       		exporter.addYUIMenuItem(exportHandler);
                       		isAnyEnabled = true;
					   	   }
                       });
                       if (isAnyEnabled) {
                         exporter.createYUIMenu('actionsMenu');
				       }
                   }

               },
               fail: function (e) {
                   console.log(e);
               }
           });

       };



   $(document).ready(function(){
        exporter.init();
    });

}));

