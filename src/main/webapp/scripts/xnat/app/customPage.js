/*
 * web: customPage.js
 * XNAT http://www.xnat.org
 * Copyright (c) 2005-2017, Washington University School of Medicine and Howard Hughes Medical Institute
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

/*!
 * Retrieve custom pages via AJAX
 * Used in: /xnat-templates/screens/Page.vm
 *
 * Page search order:
 *
 * (if a theme is active)
 * /page/themes/theme-name/page-name[.jsp]          url: /pages/view/themes/...
 * /page/themes/theme-name/page-name/content[.jsp]  url: /pages/view/themes/...
 * /themes/theme-name/pages/page-name.jsp
 * /themes/theme-name/pages/page-name/content.jsp
 * /themes/theme-name/pages/page-name.html
 * /themes/theme-name/pages/page-name/content.html
 *
 * (if no theme is set or if a page is not found in the theme)
 * /page/page-name/content[.jsp]  url: /pages/view/page-name/content
 * /page/page-name[.jsp]          url: /pages/view/page-name
 * /page/page-name/content.html
 * /page/page-name.html
 *
 */

var XNAT = getObject(XNAT);

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

    XNAT.app =
        getObject(XNAT.app || {});

    XNAT.app.customPage =
        getObject(XNAT.app.customPage || {});

    XNAT.theme =
        getObject(XNAT.theme || {});

    // add resolved theme name to XNAT.* namespace
    XNAT.themeName = XNAT.theme.name =
        XNAT.themeName || XNAT.theme.name || '';

    var themeName = XNAT.themeName;

    var customPage = XNAT.app.customPage;

    function trimSlashes(str){
        return str.replace(/^\/+|\/+$/g, '');
    }

    function isNoneTheme(name){
        var _theme = name || themeName;
        return /^none$/i.test(_theme);
    }

    var END = /\/#!?\/?/;

    // get page name for CURRENT page:
    // /page/#/page-name/#!
    // Page.vm/#/page-name/#!
    customPage.getPageName = function(url, end){
        var loc      = url || window.location.href;
        var urlParts = loc.split(/Page\.vm\/#\/|\/page\/#\/|#view=/);
        var pageName = '';
        if (urlParts.length > 1) {
            pageName = urlParts[1].split(end || END)[0];
        }
        return (customPage.pageName = escapeHtml(trimSlashes(pageName)));
    };

    customPage.getName = function(end){
        var name =
                getQueryStringValue('view') ||
                getUrlHashValue('#view=', end) ||
                getUrlHashValue('#/', end || END);
        return (customPage.name = escapeHtml(trimSlashes(name)));
    };

    // cache name of current page on load
    customPage.getName();

    customPage.getPage = function(name, container){

        // save current page name for later comparison
        var currentPage = customPage.name;
        var pagePaths   = [];
        var end         = END;

        // special handling if using the 'none' theme
        var noneTheme = isNoneTheme(themeName);

        // use an array for the name param
        // to specify a start AND end for the page string
        if (Array.isArray(name)) {
            end  = name[1] || end;
            name = name[0] || '';
        }

        // if (name && name === currentPage) {
        //     ///// RETURN /////
        //     return currentPage;
        // }

        name = name || customPage.getName(end);

        // return if there's no name or it's '!'
        if (!name || name === '!') {
            ///// RETURN /////
            return currentPage;
        }

        var $container = $$(container || customPage.container || '#view-page').html('loading...');

        function setPaths(pg){

            // remove leading and trailing slashes
            var PAGE  = trimSlashes(pg);
            var paths = [];

            // if we're using a theme (that's not the default),
            // check that theme's folder
            if (themeName && !noneTheme) {
                // jsp theme files first
                paths.push('/pages/view/themes/' + themeName + '/' + PAGE);
                paths.push('/pages/view/themes/' + themeName + '/' + PAGE + '/content');
                paths.push('/themes/' + themeName + '/pages/' + PAGE + '.jsp');
                paths.push('/themes/' + themeName + '/pages/' + PAGE + '/content.jsp');
                // html theme files next
                paths.push('/themes/' + themeName + '/pages/' + PAGE + '.html');
                paths.push('/themes/' + themeName + '/pages/' + PAGE + '/content.html');
            }

            // then core jsp and html files
            // jsp files routed through /pages/view will have '.jsp' appended automatically
            paths.push('/pages/view/' + PAGE + '/content');
            paths.push('/pages/view/' + PAGE);
            // html files need to be requested directly
            paths.push('/page/' + PAGE + '/content.html');
            paths.push('/page/' + PAGE + '.html');

            return paths;

        }

        pagePaths = setPaths(name);

        try {
            debugLog(pagePaths);
        }
        catch (e) {
            console.warn(e);
        }

        function getPage(path){
            $container.html('Loading...');
            var locParts = XNAT.url.splitUrl(window.location.href);
            return XNAT.xhr.get({
                url: XNAT.url.restUrl(path, locParts.params),
                dataType: 'html',
                success: function(content){
                    $container.html(content)
                }
            })
        }

        function lookForPage(i){
            var not_found = 'page not found';
            if (i === pagePaths.length) {
                not_found = '<b>"' + customPage.getName() + '"</b> page not found';
                $container.html(not_found);
                return false;
            }
            // recursively try to get pages in possible locations
            getPage(pagePaths[i]).fail(function(){
                lookForPage(++i)
            });
        }

        // do the stuff
        lookForPage(0);

    };

    // update the page if necessary on hash change
    $(window).on('hashchange', function(e){
        var currentPage = customPage.name;
        var newPage = customPage.getName();
        //only get a new page if the page part has changed
        if (newPage && newPage !== '!' && newPage !== currentPage) {
            e.preventDefault();
            // window.location.reload(true);
            customPage.getPage(newPage);
        }
    });

    return XNAT.app.customPage = customPage;

}));
