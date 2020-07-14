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


  exporter.open = window.openExportDialog = function(eHandler){
		alert("Camere " + eHandler);
		//Load the
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
                url: 'javascript:openExportDialog("'+ handlerObj +'")',
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
                       availableExportHandlers.forEach(function (exportHandler) {
                           exporter.addYUIMenuItem(exportHandler);
                       });
                       exporter.createYUIMenu('actionsMenu');
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

