/**
 * Initialize behavior for multi-'selectable' checkboxes contained in `container`
 *
 * @param {String|jQuery|Element} [container] - Selector string, jQuery object, or Element
 *
 * @example XNAT.app.selectableTable('#ur-container');
 * @example XNAT.app.selectableTable($('#ur-container'));
 * @example XNAT.app.selectableTable(document.getElementById('ur-container'));
 *
 */
XNAT.app.selectableTable = function selectableTable(container){

    console.log('selectableTable.js');

    var $container = $$(container || document);

    if ($container.data('selectableTable') === 'ready') {
        console.log('selectable table already initialized');
        return;
    }

    $container.addClass('selectable-table-ready').data('selectableTable', 'ready');

    var CKBX_ALL = '.selectable-select-all';
    var CKBX_ONE = '.selectable-select-one';
    var INDET    = 'indeterminate';

    var $ckbxAll = $container.find(CKBX_ALL);
    var $ckbxs   = $container.find(CKBX_ONE);

    function multichecker(){
        // if *all* checkboxes are checked, check the 'all' checkbox
        var $checked = $ckbxs.filter(':checked');
        var noneChecked = $checked.length === 0;
        var $actions;
        if (noneChecked || $checked.length === $ckbxs.length) {
            $ckbxAll.prop(INDET, false).prop('checked', !!$checked.length);
        }
        else {
            $ckbxAll.prop('checked', false).prop(INDET, true);
        }
        // if there are 'action' items, disable them if nothing is selected
        if (($actions = $container.find('.data-table-action')).length) {
            $actions[noneChecked ? 'addClass' : 'removeClass']('disabled').prop('disabled', noneChecked);
        }
    }

    function toggleAll(checked){
        $ckbxAll.prop(INDET, false).prop('checked', checked);
        // toggle only *visible* checkboxes
        $ckbxs.filter(':visible').prop('checked', checked);
    }

    // fire this on init to set the initial
    // state of the 'all' checkbox?
    multichecker();

    // namespaced event name
    var CLICK = 'click.multicheck';

    // unbind any existing 'multicheck' click handlers
    $container.off(CLICK);
    $container.off(CLICK, CKBX_ALL);
    $container.off(CLICK, CKBX_ONE);

    // delegate the main click handlers to the
    // container to handle transient elements
    $container.on(CLICK, CKBX_ALL, function(e){
        toggleAll(!!this.checked);
        multichecker();
    });
    $container.on(CLICK, CKBX_ONE, function(e){
        multichecker();
    });

    // use the inline menu toggle as the trigger for opening the menu.
    // hide the menu after a short time if the user does not mouse into the menu.
    $container.off('mouseenter', '.inline-actions-menu-container')
              .on('mouseenter', '.inline-actions-menu-container', function(){
                  var container = $(this).addClass('active');
                  var menu = container.find('.inline-actions-menu').show();
                  // if the user hovers over the container icon, but does not
                  // mouse into the popup menu, fade out the menu.
                  window.setTimeout(function(){
                      if (!menu.hasClass('active')) {
                          menu.fadeOut(150);
                          container.removeClass('active');
                      }
                  }, 1500);
              });

    $container.off('mouseenter', '.inline-actions-menu')
              .on('mouseenter', '.inline-actions-menu', function(){
                  $(this).addClass('active');
              });

    $container.off('mouseleave', '.inline-actions-menu')
              .on('mouseleave', '.inline-actions-menu', function(){
                  $(this).hide().removeClass('active');
              });

    $container.off('mouseleave', '.inline-actions-menu-container')
              .on('mouseleave', '.inline-actions-menu-container', function(){
                  $(this).removeClass('active').find('.inline-actions-menu').hide();
              });

};

// if '.data-table-container' is available when the DOM loads,
// it will be used as the container, otherwise `document` will be used
// $(function(){
//     XNAT.app.selectableTable(document.querySelector('.data-table-container'));
// });
