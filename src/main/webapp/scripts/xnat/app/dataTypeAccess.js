/*!
 * Get datatype objects depending on access type
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

    var undef, dataTypeAccess;
    var displayItems = [];
    var displayName = 'browseable';
    var SITE_ROOT = XNAT.url.rootUrl();
    var USERNAME = window.username || 'xnat';

    XNAT.app =
        getObject(XNAT.app || {});

    XNAT.app.dataTypeAccess = dataTypeAccess =
        getObject(XNAT.app.dataTypeAccess || {});


    dataTypeAccess.isReady = false;
    dataTypeAccess.url = XNAT.url.rootUrl('/xapi/access/displays');

    function dataTypeAccessUrl(part){
        return dataTypeAccess.url + (part ? ('/' + part) : '')
    }

    dataTypeAccess.setUrl = dataTypeAccessUrl;

    // this variable is set in BaseJS.vm
    dataTypeAccess.modified = XNAT.cacheLastModified = XNAT.cacheLastModified || cacheLastModified;


    var ACCESS_DISPLAYS_MODIFIED = 'ACCESS_DISPLAYS_MODIFIED.' + USERNAME;
    dataTypeAccess.modifiedCookie = ACCESS_DISPLAYS_MODIFIED;

    dataTypeAccess.needsUpdate = false;

    // if there's a cookie, get the value, compare, and set a flag to update later
    if (XNAT.cookie.exists(ACCESS_DISPLAYS_MODIFIED)) {
        // set a simple boolean to indicate an update needs to be made when setting up data type access
        dataTypeAccess.needsUpdate = (XNAT.cookie.get(ACCESS_DISPLAYS_MODIFIED) != dataTypeAccess.modified);
    }
    dataTypeAccess.needsUpdate = /true|all/i.test(getQueryStringValue('updateAccess')) || dataTypeAccess.needsUpdate;
    // always set the cookie (after checking above)
    XNAT.cookie.set(ACCESS_DISPLAYS_MODIFIED, dataTypeAccess.modified);

    // get the list of display types synchronously (fast)
    dataTypeAccess.displays = [
        'browseable',
        'browseableCreateable',
        'createable',
        'searchable',
        'searchableByDesc',
        'searchableByPluralDesc'
    ];

    // save the display type list as a cookie as well
    // XNAT.cookie.set('ACCESS_DISPLAYS.' + USERNAME, dataTypeAccess.displays.join('|'));


    return (XNAT.app.dataTypeAccess = dataTypeAccess);

}));
