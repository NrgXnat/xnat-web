/*
 * web: userCacheFileManager.js
 * XNAT http://www.xnat.org
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

    var userCacheFileManager;
    XNAT.app = getObject(XNAT.app || {});
    XNAT.app.userCacheFileManager = userCacheFileManager = getObject(XNAT.app.userCacheFileManager || {});

    var sourceStructure;
    var destinationStructure;
    const CACHE_TREE_ROOT_NODE = "My Uploads";
    const ARCHIVE_TREE_ROOT_NODE = "Projects";
    var associatedTreeStructure = {};
    var userData = {};

    userData.projects = [];
    userData.project_resources = [];
    userData.subjects = [];
    userData.subject_resources = [];
    userData.sessions = [];
    userData.session_resources = [];
    userData.scans = [];
    userData.scan_resources = [];

    class TreeViewer {
        constructor(data, container) {
            this.data = data;
            this.container = container;
            this.expandedNodes = new Set();
            this.init();
        }

        init() {
            this.render();
            this.updateStats();
        }

        formatSize(bytes) {
            if (!bytes) return '';
            const units = ['B', 'KB', 'MB', 'GB'];
            let size = bytes;
            let unit = 0;
            while (size >= 1024 && unit < units.length - 1) {
                size /= 1024;
                unit++;
            }
            return String(size.toFixed(1) + units[unit]);
        }

        formatDate(dateString) {
            if (!dateString) return '';
            return new Date(dateString).toLocaleDateString();
        }

        hasChildren(node) {
            return node.children && node.children.length > 0;
        }

        isExpanded(nodeId) {
            return this.expandedNodes.has(nodeId);
        }

        toggleExpand(nodeId) {
            if (this.expandedNodes.has(nodeId)) {
                this.expandedNodes.delete(nodeId);
            } else {
                this.expandedNodes.add(nodeId);
            }
        }

        generateNodeId(node, path = '') {
            return (path + '/' + node.name + '-' + node.destPath).replace(/^\//, '');
        }

        createTreeNode(node, path = '', level = 0) {
            const nodeId = this.generateNodeId(node, path);
            const hasChildren = this.hasChildren(node);
            const isExpanded = this.isExpanded(nodeId);
            const isFolder = node.type === 'folder';

            const nodeDiv = document.createElement('div');
            nodeDiv.className = 'tree-node';

            const itemDiv = document.createElement('div');
            itemDiv.className = 'tree-item';

            // Expand/collapse icon
            const expandIcon = document.createElement('span');
            expandIcon.className = `expand-icon ${hasChildren ? (isExpanded ? 'expanded fa fa-minus' : ' fa fa-plus') : 'no-children'}`;

            if (hasChildren) {
                expandIcon.addEventListener('click', (e) => {
                    e.stopPropagation();
                    this.toggleExpand(nodeId);
                    this.render();
                });
            }

            const icon = document.createElement('span');
            icon.className = isFolder ? 'file-icon fa fa-folder' : 'file-icon fa fa-file';

            const name = document.createElement('span');
            name.className = 'item-name';
            name.textContent = node.name;

            const info = document.createElement('span');
            info.className = 'item-info ' + (isFolder ? '' : 'file-info');

            let infoText = [];
            if (node.size) infoText.push(this.formatSize(node.size));
            info.textContent = infoText.join(' ');

            itemDiv.appendChild(expandIcon);
            itemDiv.appendChild(icon);
            itemDiv.appendChild(name);
            if (infoText.length > 0) {
                itemDiv.appendChild(info);
            }

            nodeDiv.appendChild(itemDiv);

            if (node.destPath) {
                const pathDiv = document.createElement('div');
                pathDiv.className = 'tree-path';
                pathDiv.textContent = node.destPath || '';
                nodeDiv.appendChild(pathDiv);
            }

            if (hasChildren) {
                const childrenDiv = document.createElement('div');
                childrenDiv.className = 'tree-children ' + (isExpanded ? 'expanded' : '');

                node.children.forEach(child => {
                    const childNode = this.createTreeNode(child, nodeId, level + 1);
                    childrenDiv.appendChild(childNode);
                });

                nodeDiv.appendChild(childrenDiv);
            }

            return nodeDiv;
        }

        render() {
            this.container.innerHTML = '';
            this.data.forEach(rootNode => {
                const treeNode = this.createTreeNode(rootNode);
                this.container.appendChild(treeNode);
            });
        }

        countNodes(nodes) {
            let folders = 0;
            let files = 0;
            let totalSize = 0;

            const traverse = (nodeList) => {
                nodeList.forEach(node => {
                    if (node.type === 'folder') {
                        folders++;
                    } else {
                        files++;
                    }
                    if (node.size) {
                        totalSize += node.size;
                    }
                    if (node.children) {
                        traverse(node.children);
                    }
                });
            };

            traverse(nodes);
            return { folders, files, totalSize };
        }

        updateStats() {
            const stats = this.countNodes(this.data);
           // const statsDiv = document.getElementById('stats');
           // statsDiv.innerHTML = `
           //     <strong>Summary:</strong>
           //     ${stats.folders} folders,
           //     ${stats.files} files,
           //     Total size: ${this.formatSize(stats.totalSize)}
           // `;
           console.log(JSON.stringify(stats));
        }
    }

    let isUploading = false;
    let isMinimized = false;

    userCacheFileManager.formatFileSize = function(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    userCacheFileManager.generateTimestamp = function() {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const seconds = String(now.getSeconds()).padStart(2, '0');

        return `${year}${month}${day}-${hours}${minutes}${seconds}`;
    }

    userCacheFileManager.uploadFileToCache = async function(file) {
        if (isUploading) return;

        isUploading = true;
        const uploadArea = document.querySelector('.uce-upload-area');
        const uploadBtn = document.getElementById('uploadBtn');
        const cancelUploadButton = document.getElementById('cancelUploadButton');

        uploadArea.classList.add('uce-uploading');
        uploadBtn.disabled = true;
        cancelUploadButton.disabled = true;
        xmodal.loading.open({ title: 'Uploading data to cache...'});

        try {
            const timestamp = userCacheFileManager.generateTimestamp();
            const filename = encodeURIComponent(file.name);
            const endpoint = serverRoot + '/data/user/cache/resources/' + timestamp + '/files/' + filename;
            let uploadUrl = XNAT.url.csrfUrl(endpoint,{extract: true},false,false);
            // Use Fetch API with file in body
            const formData = new FormData();
            formData.append('file', file);
            const response = await fetch(uploadUrl, {
                method: 'PUT',
                body: formData
            });

            xmodal.loading.close();

            if (!response.ok) {
                throw new Error('Upload failed with status ' + response.status + ' : ' + response.statusText);
            }

            XNAT.ui.banner.top(3000,'File uploaded successfully to: ' + endpoint,'success');
            userCacheFileManager.updateSourceTree(false);
            console.log('Upload of file ' + filename + ' to user cache successful');
            userCacheFileManager.resetUploadWidget();

        } catch (error) {
           console.error('Upload error:', error);
           xmodal.loading.close();

           let errorMessage = 'Upload failed';
           if (error.name === 'TypeError' && error.message.includes('fetch')) {
               errorMessage = 'Network error - check your connection';
           } else if (error.message.includes('status')) {
               errorMessage = error.message;
           } else {
               errorMessage = 'Upload failed: ' + error.message;
           }

           XNAT.ui.banner.top(3000, errorMessage, 'error');
           userCacheFileManager.resetUploadWidget();
        }
    }

    userCacheFileManager.resetUploadWidget = function() {
        isUploading = false;
        selectedZipFile = null;

        const uploadArea = document.querySelector('.uce-upload-area');
        const uploadBtn = document.getElementById('uploadBtn');
        const cancelUploadButton = document.getElementById('cancelUploadButton');
        const zipFileInput = document.getElementById('zipFile');

        uploadArea.classList.remove('uce-uploading');
        uploadBtn.disabled = false;
        cancelUploadButton.disabled = false;
        zipFileInput.value = '';

        document.getElementById('uploadControls').classList.remove('uce-show');
    }

    userCacheFileManager.toggleMinimize = function() {
        const uploadWidget = document.getElementById('uploadWidget');
        const uploadContent = document.getElementById('uploadContent');
        const minimizeBtn = document.getElementById('minimizeBtn');

        isMinimized = !isMinimized;

        if (isMinimized) {
            uploadContent.classList.add('uce-minimized');
            uploadWidget.classList.add('uce-minimized');
            minimizeBtn.innerHTML = '<i class="fa fa-plus-circle"></i>';
            minimizeBtn.title = 'Maximize';
        } else {
            uploadContent.classList.remove('uce-minimized');
            uploadWidget.classList.remove('uce-minimized');
            minimizeBtn.innerHTML = '<i class="fa fa-minus-circle"></i>';
            minimizeBtn.title = 'Minimize';
        }
    }

    userCacheFileManager.updateSelectedFileDisplay = function(file) {
        const fileNameEl = document.getElementById('selectedFileName');
        const fileSizeEl = document.getElementById('selectedFileSize');

        fileNameEl.textContent = file.name;
        fileSizeEl.textContent = userCacheFileManager.formatFileSize(file.size);
        document.getElementById('uploadControls').classList.add('uce-show');
    }

    document.getElementById('zipFile').addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            selectedZipFile = file;
            userCacheFileManager.updateSelectedFileDisplay(file);
        }
    });

    document.getElementById('uploadBtn').addEventListener('click', (e) => {
        e.stopPropagation();
        if (selectedZipFile && !isUploading) {
            userCacheFileManager.uploadFileToCache(selectedZipFile);
        }
    });

    document.getElementById('cancelUploadButton').addEventListener('click', (e) => {
        e.stopPropagation();
        if (selectedZipFile && !isUploading) {
            userCacheFileManager.resetUploadWidget();
        }
    });

    document.getElementById('minimizeBtn').addEventListener('click', (e) => {
        e.stopPropagation();
        userCacheFileManager.toggleMinimize();
    });

    const uploadArea = document.querySelector('.uce-upload-area');
    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.classList.add('dragover');
    });

    uploadArea.addEventListener('dragleave', () => {
        uploadArea.classList.remove('dragover');
    });

    uploadArea.addEventListener('click', (e) => {
        if (isMinimized) {
            e.preventDefault();
            e.stopPropagation();
            userCacheFileManager.toggleMinimize();
            return;
        }
    });

    uploadArea.addEventListener('drop', (e) => {
        e.preventDefault();
        uploadArea.classList.remove('dragover');
        const files = Array.from(e.dataTransfer.files);
        const zipFiles = files.filter(f => f.name.toLowerCase().endsWith('.zip'));

        if (zipFiles.length > 0) {
            selectedZipFile = zipFiles[0];
            userCacheFileManager.updateSelectedFileDisplay(selectedZipFile);

            const dt = new DataTransfer();
            dt.items.add(selectedZipFile);
            document.getElementById('zipFile').files = dt.files;
        } else {
            XNAT.ui.banner.top(3000, 'Please only place ZIP files into the upload area.', 'error');
        }
    });

    userCacheFileManager.fetchData = function(url) {
        let dataUrl = XNAT.url.restUrl(url,{format: 'json'},false,false);
        var responseData = {};
        responseData["ResultSet"] = {};
        responseData["ResultSet"]["Result"] = [];
        XNAT.xhr.get({
            url: dataUrl,
            async: false,
            success: function (data) {
                responseData = data;
            },
            fail: function (e) {
                XNAT.ui.banner.top(5000, 'Unable to fetch data from: ' + url, 'error');
            }
        });
        return responseData;
    }

    userCacheFileManager.sortAlphabetically = function(data) {
        data.sort((a, b) => {
            var nameA, nameB;
            if (a.name) {
                nameA = a.name.toUpperCase();
                nameB = b.name.toUpperCase();
            } else if (a.label) {
                nameA = a.label.toUpperCase();
                nameB = b.label.toUpperCase();
            } else {
                nameA = a.ID.toUpperCase();
                nameB = b.ID.toUpperCase();
            }
            if (nameA < nameB) {
                return -1;
            }
            if (nameA > nameB) {
                return 1;
            }
            return 0;
        });
    }

    userCacheFileManager.fetchProjects = async function() {
        if (userData['projects'].length == 0) {
          let response = userCacheFileManager.fetchData('/data/projects');
          userData['projects'].push(...response['ResultSet']['Result']);
          userCacheFileManager.sortAlphabetically(userData['projects']);
        }
        return userData['projects'] || [];
    }

    userCacheFileManager.fetchXnatDataAtLevel = function(inputUri, inputDataLevel, apiBaseUrl) {
        if(!userData[inputDataLevel].hasOwnProperty(inputUri)) {
            let response = userCacheFileManager.fetchData(inputUri + apiBaseUrl)
            userData[inputDataLevel][inputUri] = [];
            userData[inputDataLevel][inputUri].push(...response['ResultSet']['Result']);
            userCacheFileManager.sortAlphabetically(userData[inputDataLevel][inputUri]);
        }
        return userData[inputDataLevel][inputUri] || [];
    }

    userCacheFileManager.fetchResourcesAtLevel = function(uri, input_resource_level) {
        if(!userData[input_resource_level].hasOwnProperty(uri)) {
            let response = userCacheFileManager.fetchData(uri +  '/resources');
            userData[input_resource_level][uri] = [];
            userData[input_resource_level][uri].push(...response['ResultSet']['Result']);
        }
        return userData[input_resource_level][uri] || [];
    }

    userCacheFileManager.submitToIngest = async function(jsonData) {
        try {
            const response = await fetch('/xapi/ingest', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(jsonData)
            });

            if (!response.ok) {
                throw new Error('HTTP error! status: ' + response.status);
            }

            const result = await response.json();
            console.log('Success:', result);
            return result;
        } catch (error) {
            console.error('Error submitting data:', error);
            throw error;
        }
    }

    let expandedSourceFolders = new Set();
    let expandedDestinationFolders = new Set();
    let filteredDestinationFolders = {};
    let selectedPath = {};
    let selectLevels = ['project', 'project_resource', 'subject', 'subject_resource', 'session', 'session_resource', 'scan', 'scan_resource'];
    let droppedFiles = [];

    function getFileIcon(fileName) {
        const ext = fileName.split('.').pop().toLowerCase();
        const icons = {
            'txt': '<i class="fa fa-file-text-o"></i>',
            'pdf': '<i class="fa fa-file-pdf-o"></i>',
            'html': '<i class="fa fa-file-text-o"></i>',
            'css': '<i class="fa fa-file-text-o"></i>',
            'js': '<i class="fa fa-file-text-o"></i>',
            'jpg': '<i class="fa fa-file-image-o"></i>',
            'png': '<i class="fa fa-file-image-o"></i>',
            'gif': '<i class="fa fa-file-image-o"></i>',
            'docx': '<i class="fa fa-file-word-o"></i>',
            'pptx': '<i class="fa fa-file-ppt-o"></i>',
            'xlsx': '<i class="fa fa-file-xls-o"></i>',
            'json': '<i class="fa fa-file-text-o"></i>',
            'zip': '<i class="fa fa-file-zip-o"></i>',
            'exe': '<i class="fa fa-file-code-o"></i>'
        };
        return icons[ext] || '<i class="fa fa-file"></i>';
    }

    userCacheFileManager.createDragEvents = function(div) {
        div.addEventListener('dragstart', (e) => {
            XNAT.app.userCacheFileManager.handleDragStart(event)
        });
        div.addEventListener('dragend', (e) => {
            XNAT.app.userCacheFileManager.handleDragEnd(event)
        });
    }

    userCacheFileManager.validateForm = function() {
        const resourceLevel = document.getElementById('resourceLevel').value;
        const project = document.getElementById('projectSelect').value;
        const subject = document.getElementById('subjectSelect').value;
        const session = document.getElementById('sessionSelect').value;
        const scan = document.getElementById('scanSelect').value;
        const resourceName = document.getElementById('resourceSelect').value.trim();

        let isValid = false;

        if (resourceLevel && project && resourceName) {
            switch (resourceLevel) {
                case 'project':
                    isValid = true;
                    break;
                case 'subject':
                    isValid = subject !== '';
                    break;
                case 'session':
                    isValid = subject !== '' && session !== '';
                    break;
                case 'scan':
                    isValid = subject !== '' && session !== '' && scan !== '';
                    break;
            }
        }

        document.getElementById('ingestBtn').disabled = !isValid;
    }

    userCacheFileManager.fetchUserCacheFiles = function() {
        let userCacheFileUrl = XNAT.url.restUrl('data/user/cache/resources?format=treeJson',{},false,false);
        XNAT.xhr.get({
            url: userCacheFileUrl,
            async: false,
            success: function (data) {
                sourceStructure = data;
            },
            fail: function (e) {
                XNAT.ui.banner.top(5000, 'Unable to fetch user cache files.', 'error');
            }
        });
    }

    userCacheFileManager.createDeleteButton = function(folderPath) {
        return spawn('button.btn.btn-sm.delete-cache-element', {
            onclick: function (e) {
                XNAT.app.userCacheFileManager.removeFileFromCache(folderPath, e.currentTarget.parentElement.dataset.absolutePath);
            },
            title: "Delete from cache",
            style: {color: 'black', border: 'none', cursor: 'pointer'}
        }, [spawn('i.fa.fa-trash')]);
    }

    userCacheFileManager.updateSourceTree = function(initialRendering) {
        userCacheFileManager.fetchUserCacheFiles();
        expandedSourceFolders.add(CACHE_TREE_ROOT_NODE);
        if (!initialRendering) {
            $('#sourceTree').empty();
        }
        $('#sourceTree').append(userCacheFileManager.renderSourceTree(sourceStructure, true));
    }

    userCacheFileManager.renderSourceTree = function(node, enableDrag, path = "", level = 0) {
        let currentLevelDiv = spawn('div');
        const folderPath = path + node.name;
        let includeDelete = enableDrag;
        if (node.type === 'folder') {
            const isRootNode = node.name === CACHE_TREE_ROOT_NODE;
            const isExpanded = (enableDrag ? expandedSourceFolders.has(path + node.name) : expandedDestinationFolders.has(path + node.name) );

            let folderDiv = spawn('div');
            $(folderDiv).attr({'class': "uce-folder-item source-folder-item" + (isExpanded ? " expanded" : ""), 'data-path': folderPath,
               'data-filename': node.name, 'data-type': node.type, 'data-absolute-path': node.absolutePath, 'draggable': enableDrag})
            if (enableDrag) {
                userCacheFileManager.createDragEvents(folderDiv);
            }
            folderDiv.append(spawn('span|class=uce-folder-toggle', {
                onclick: function (e) {
                    XNAT.app.userCacheFileManager.toggleFolder(event, folderPath, enableDrag, expandedSourceFolders);
                },
                style: {cursor: 'pointer'},
                'html': isExpanded ? '<i class="fa fa-folder-open"></i>' : '<i class="fa fa-folder"></i>'
            }));
            folderDiv.append(spawn('span|class=uce-folder-name', {
                'html': node.name
            }));

            if (!isRootNode && includeDelete) {
                folderDiv.append(userCacheFileManager.createDeleteButton(folderPath));
            }

            currentLevelDiv.append(folderDiv);
            if (isExpanded && node.children) {
                let childrenDiv = spawn('div|class=uce-children');
                for (let i = 0; i < node.children.length; i++){
                    let child = node.children[i];
                    $(childrenDiv).append(userCacheFileManager.renderSourceTree(child, enableDrag, path + node.name + '/', level + 1))
                }
                $(currentLevelDiv).append(childrenDiv);
            }
        } else  {
            let fileDiv = spawn('div');
            $(fileDiv).attr({'class': "uce-folder-item source-file-item", 'data-path': path + node.name, 'data-filename': node.name,
                'data-type': node.type, 'data-absolute-path': node.absolutePath, 'draggable': enableDrag})
            if (enableDrag) {
                userCacheFileManager.createDragEvents(fileDiv);
            }
            fileDiv.append(spawn('span|class=uce-icon', {
                'html': getFileIcon(node.name)
            }));
            fileDiv.append(spawn('span|class=uce-file-name', {
                'html': node.name
            }));
            if (includeDelete) {
                fileDiv.append(userCacheFileManager.createDeleteButton(folderPath));
            }
            currentLevelDiv.append(fileDiv);
        }
        return currentLevelDiv;
    }

    userCacheFileManager.updateDestinationTree = async function() {
        var projects = await userCacheFileManager.fetchProjects();
        const treeContainer = document.getElementById('destinationTree');
        destinationStructure = userCacheFileManager.convertXnatUserDataToFileTree();
        expandedDestinationFolders.add(ARCHIVE_TREE_ROOT_NODE);
        treeContainer.append(userCacheFileManager.renderDestinationTree(destinationStructure));
    }

    userCacheFileManager.convertXnatUserDataToFileTree =  function() {
        var fileTree = {name: ARCHIVE_TREE_ROOT_NODE, type: "folder", xnatType: "archive", uri: ""};
        fileTree.children = [];
        userData['projects'].forEach(project => {
            fileTree.children.push({name: project.name, type: "folder", xnatType: "project", uri: project.URI});
        });
        return fileTree;
    }

    userCacheFileManager.createDestinationTreeButton = function(buttonAction, nodeUri, icon, title) {
        let destButton =  spawn('button.btn.btn-sm', {
            onclick: function (e) {
                buttonAction(this);
            },
            title: title,
            style: {color: 'black', border: 'none', cursor: 'pointer'},
        }, [spawn(icon)]);
        $(destButton).attr({'data-uri': nodeUri})
        return destButton;
    }

    userCacheFileManager.createFilterElement = (event) => {
        if (event.key != "Enter") {
            return;
        }
        let filter = event.target.value;
        let folderElementPath = event.currentTarget.parentElement.getAttribute('data-path');
        if (filter === '') {
            delete filteredDestinationFolders[folderElementPath];
        } else {
            filteredDestinationFolders[folderElementPath] = filter;
        }
        $('#destinationTree').empty();
        $('#destinationTree').append(userCacheFileManager.renderDestinationTree(destinationStructure));
    }

    userCacheFileManager.renderDestinationTree = function(node, path = "", level = 0) {
        let currentLevelDiv = spawn('div');
        const folderPath = path + node.name;

        if (node.type === 'folder') {
            const isExpanded = expandedDestinationFolders.has(path + node.name);

            let folderDiv = spawn('div');
            $(folderDiv).attr({'class': "uce-folder-item destination-folder-item" + (isExpanded ? " expanded" : ""), 'data-path': folderPath,
               'data-xnat-type': node.xnatType, 'data-uri': node.uri, 'data-name': node.name})
            folderDiv.append(spawn('span|class=uce-folder-toggle', {
                onclick: function (e) {
                    XNAT.app.userCacheFileManager.toggleFolder(event, folderPath, false, expandedDestinationFolders);
                },
                'html': isExpanded ? '<i class="fa fa-minus"></i>' : '<i class="fa fa-plus"></i>'
            }));
            folderDiv.append(spawn('span|class=uce-folder-name', {
                'html': node.name
            }));
            if (node.name === 'Resources' || node.name === 'Projects' || node.name === 'Subjects' || node.name === 'Experiments' || node.name === 'Scans') {
                let inputFilterValue = '';
                if (filteredDestinationFolders.hasOwnProperty(folderPath)) {
                    inputFilterValue = filteredDestinationFolders[folderPath];
                }
                let filterInput = spawn('input|class=filter-input', {
                    name: 'filter_folder',
                    placeholder: 'Filter folder elements',
                    title: 'Use the enter key to filter folder contents',
                    value: inputFilterValue
                })
                filterInput.addEventListener('keyup', userCacheFileManager.createFilterElement);
                folderDiv.append(filterInput);
            }

            if (node.name === 'Resources') {
                folderDiv.append(userCacheFileManager.createDestinationTreeButton(XNAT.app.userCacheFileManager.addNewResource, node.uri, 'i.fa.fa-folder', 'Add Resource'))
            } else if (node.name === 'Subjects') {
                folderDiv.append(userCacheFileManager.createDestinationTreeButton(XNAT.app.userCacheFileManager.addNewSubject, node.uri, 'i.fa.fa-user-plus', 'Add Subject'))
            }  else if (node.name === 'Experiments') {
                folderDiv.append(userCacheFileManager.createDestinationTreeButton(XNAT.app.userCacheFileManager.addNewExperiment, node.uri, 'i.fa.fa-flask', 'Add Experiment'))
            }  else if (node.name === 'Scans') {
                folderDiv.append(userCacheFileManager.createDestinationTreeButton(XNAT.app.userCacheFileManager.addNewScan, node.uri, 'i.fa.fa-qrcode', 'Add Scan'))
            }
            currentLevelDiv.append(folderDiv);

            if (isExpanded && node.children) {
                let childrenDiv = spawn('div|class=uce-children');
                for (let i = 0; i < node.children.length; i++){
                    let child = node.children[i];
                    if (filteredDestinationFolders.hasOwnProperty(folderPath)) {
                        if (child.name.toLowerCase().includes(filteredDestinationFolders[folderPath].toLowerCase())) {
                             $(childrenDiv).append(userCacheFileManager.renderDestinationTree(child, path + node.name + '/', level + 1));
                        }
                    } else {
                        $(childrenDiv).append(userCacheFileManager.renderDestinationTree(child, path + node.name + '/', level + 1));
                    }
                }
                $(currentLevelDiv).append(childrenDiv);
            }
        } else {
            let dropZoneDiv = spawn('div');
            $(dropZoneDiv).attr({'class': "uce-drop-zone", 'data-path': path + node.name,
                'data-filename': node.name, 'data-uri': node.uri, 'title': "Drop files here to add data to: " + node.uri})
            dropZoneDiv.append(spawn('span', {
                'html': '<i class="fa fa-dropbox"></i>'
            }));
            dropZoneDiv.append(spawn('span', {
                'html': ' Drop files here for ' + node.name
            }));
            currentLevelDiv.append(dropZoneDiv);
        }
        return currentLevelDiv;
    }

    userCacheFileManager.toggleFolder = function(event, folderPath, isSourcePane, expandedFoldersList) {
        event.preventDefault();
        const folderElement = event.currentTarget.parentElement;
        const isExpanded = folderElement.getAttribute('data-expanded') === 'true';

        if (isExpanded) {
            userCacheFileManager.collapseFolder(folderElement, folderPath);
        } else {
            userCacheFileManager.expandFolder(folderElement, folderPath, isSourcePane, expandedFoldersList);
        }
    }

    userCacheFileManager.expandFolder = function(folderElement, folderPath, isSourcePane, expandedFoldersList) {
        folderElement.setAttribute('data-expanded', 'true');
        if (expandedFoldersList.has(folderPath)) {
                expandedFoldersList.delete(folderPath);
        } else {
            expandedFoldersList.add(folderPath);
            if (!isSourcePane) {
                userCacheFileManager.loadNode(folderElement);
            }
        }
        if (isSourcePane) {
            $('#sourceTree').empty();
            $('#sourceTree').append(userCacheFileManager.renderSourceTree(sourceStructure, true));
        } else {
            $('#destinationTree').empty();
            $('#destinationTree').append(userCacheFileManager.renderDestinationTree(destinationStructure));
            userCacheFileManager.setupDropZone();
        }
        folderElement.style.backgroundColor = '#e8f4fd';
    }

    userCacheFileManager.collapseFolder = function(folderElement, folderPath) {
        folderElement.setAttribute('data-expanded', 'false');
        folderElement.style.backgroundColor = '';
    }


    userCacheFileManager.handleDragStart = function(e) {
        const fileName = e.target.dataset.filename;
        const filePath = e.target.dataset.path;
        const fileType = e.target.dataset.type;
        const absolutePath = e.target.dataset.absolutePath;

        e.dataTransfer.setData('text/plain', JSON.stringify({
            name: fileName,
            path: filePath,
            type: fileType,
            absolutePath: absolutePath
        }));

        e.target.classList.add('dragging');
    }

    userCacheFileManager.handleDragEnd = function(e) {
        e.target.classList.remove('dragging');
    }

    userCacheFileManager.setupDropZone = function() {
        const dropZoneDivs = document.querySelectorAll('.uce-drop-zone');
        dropZoneDivs.forEach(dropZoneDiv => {
            dropZoneDiv.addEventListener('dragover', (e) => {
                e.preventDefault();
                dropZoneDiv.style.borderColor = '#667eea';
                dropZoneDiv.style.background = '#f8f9ff';
            });
            dropZoneDiv.addEventListener('dragleave', (e) => {
                e.preventDefault();
                dropZoneDiv.style.borderColor = '#ddd';
                dropZoneDiv.style.background = 'transparent';
            });
            dropZoneDiv.addEventListener('drop', (e) => {
                e.preventDefault();
                dropZoneDiv.style.borderColor = '#667eea';
                dropZoneDiv.style.background = 'rgba(79, 172, 254, 0.3)';
                const fileData = JSON.parse(e.dataTransfer.getData('text/plain'));
                userCacheFileManager.handleFileDrop(fileData, dropZoneDiv.getAttribute('data-uri'));
            });
        });
    }

    userCacheFileManager.handleFileDrop = function (fileData, destinationPath) {
        const fileInfo = {
            name: fileData.name,
            type: fileData.type,
            sourcePath: fileData.path,
            destPath: destinationPath,
            absolutePath: fileData.absolutePath,
            children: [],
            status: 'Associated'
        };

        let fileAlreadyAssociated = false;
        droppedFiles.forEach(file => {
            if (file.absolutePath === fileInfo.absolutePath && file.destPath === fileInfo.destPath) {
                fileAlreadyAssociated = true;
            }
        })

        if (!fileAlreadyAssociated) {
            droppedFiles.push(fileInfo);
            userCacheFileManager.updateFileCount();
            XNAT.ui.banner.top(3000, fileData.name  + ' associated with ' + destinationPath, 'success');
        } else {
            XNAT.ui.banner.top(3000, fileData.name  + ' has already been associated with ' + destinationPath + '. Please check the file tree for further information.', 'error');
        }
    }

    userCacheFileManager.updateAssociatedFileTree = function() {
        let populatedJsonArray = userCacheFileManager.populateFolderChildren(droppedFiles);
        const associatedTree = document.getElementById('associatedTree');
        const treeViewer = new TreeViewer(populatedJsonArray, associatedTree);
        userCacheFileManager.show(associatedTree);
        userCacheFileManager.hide(document.getElementById('destinationTree'));
    }

    userCacheFileManager.hide = function(element) {
        element.style.display = 'none';
    }

    userCacheFileManager.show = function(element) {
        element.style.display = 'block';
    }

    userCacheFileManager.findChildrenByAbsolutePath = function(absolutePath) {
        if (Array.isArray(sourceStructure)) {
            for (let item of sourceStructure) {
                const result = userCacheFileManager.searchNodeByAbsolutePath (item, absolutePath);
                if (result) {
                    return result;
                }
            }
        } else {
            // Handle case where sourceStructure is a single object
            const result = userCacheFileManager.searchNodeByAbsolutePath(sourceStructure, absolutePath);
            if (result) {
                return result;
            }
        }
        return [];
    }

    userCacheFileManager.searchNodeByAbsolutePath = function(node, targetAbsolutePath) {
        if (node.absolutePath === targetAbsolutePath) {
            return node.children || [];
        }

        if (node.children && Array.isArray(node.children)) {
            for (let child of node.children) {
                const result = userCacheFileManager.searchNodeByAbsolutePath(child, targetAbsolutePath);
                if (result) {
                    return result;
                }
            }
        }
        return null;
    }

    userCacheFileManager.getParentFolderName = function (absolutePath) {
        // Remove trailing slash if present
        const cleanPath = absolutePath.replace(/\/$/, '');
        const pathParts = cleanPath.split('/');
        return pathParts[pathParts.length - 1];
    }

    userCacheFileManager.populateFolderChildren = function(jsonArray) {
        let updatedJsonArray = [];
        jsonArray.forEach(item => {
            if (item.type === 'folder') {
                if (item.absolutePath && sourceStructure) {
                    const folderChildren = userCacheFileManager.findChildrenByAbsolutePath(item.absolutePath);
                    const parentFolderName = userCacheFileManager.getParentFolderName(item.absolutePath);
                    if (folderChildren && Array.isArray(folderChildren) && folderChildren.length > 0) {
                        folderChildren.forEach(child => {
                            const childDestPath = item.destPath + '/' + parentFolderName + '/' + child.name;
                            const childItem = {
                                ...child,
                                destPath: childDestPath
                            };
                            item.children.push(childItem);
                        });
                    }
                }
            }
            updatedJsonArray.push(item);
        });
        return updatedJsonArray;
    }

    userCacheFileManager.convertToTree = function(jsonArray) {
        const tree = {};

        jsonArray.forEach(item => {
            const pathSegments = item.destPath.split('/');
            let currentNode = tree;

            pathSegments.forEach((segment, index) => {
                if (!currentNode[segment]) {
                    currentNode[segment] = {
                        type: 'folder',
                        children: {},
                        files: []
                    };
                }

                if (index === pathSegments.length - 1) {
                    if (item.type === 'folder') {
                        currentNode[segment].name = item.name;
                        currentNode[segment].type = item.type;
                        currentNode[segment].sourcePath = item.sourcePath;
                        currentNode[segment].destPath = item.destPath;
                        currentNode[segment].status = item.status;
                        currentNode[segment].absolutePath = item.absolutePath;

                        if (item.absolutePath && sourceStructure) {
                            const folderChildren = userCacheFileManager.findChildrenByAbsolutePath(item.absolutePath);
                            const parentFolderName = userCacheFileManager.getParentFolderName(item.absolutePath);
                            if (folderChildren && Array.isArray(folderChildren) && folderChildren.length > 0) {
                                folderChildren.forEach(child => {
                                    const childDestPath = item.destPath + '/' + parentFolderName + '/' + child.name;
                                    const childItem = {
                                        ...child,
                                        destPath: childDestPath
                                    };

                                    jsonArray.push(childItem);
                                });
                            }
                        }
                    } else {
                        currentNode[segment].files.push({
                            name: item.name,
                            type: item.type,
                            sourcePath: item.sourcePath,
                            destPath: item.destPath,
                            status: item.status,
                            absolutePath: item.absolutePath
                        });
                    }
                } else {
                    currentNode = currentNode[segment].children;
                }
            });
        });
        return tree;
    }

    userCacheFileManager.displayTree = function(tree, indent = '') {
        let result = '';
        let folderIcon = '<i class="fa fa-folder"></i>';
        let fileIcon = '<i class="fa fa-file"></i>';
        Object.keys(tree).forEach(key => {
            if (tree[key].type === 'folder') {
                result += `<div style="padding-left: ${indent.length * 10}px;">${folderIcon} ${key}/</div>`;
                if (tree[key].files.length > 0) {
                    tree[key].files.forEach(file => {
                        var icon = fileIcon;
                        if (file.type === 'folder') {
                            icon = folderIcon;
                        }
                        result += `<div style="padding-left: ${(indent.length + 2) * 10}px;">${icon}${file.name}
                            <button onclick="XNAT.app.userCacheFileManager.removeFile('${file.name}', '${file.sourcePath}', '${file.status}')"
                            style="padding: 0;  color: red; border: none;  cursor: pointer;"
                            title="Remove file">
                            <i class="fa fa-times"></i>
                            </button>
                            </div>`;
                    });
                }
                result += userCacheFileManager.displayTree(tree[key].children, indent + '  ');
            } else {
                result += `<div style="padding-left: ${indent.length * 10}px;">${fileIcon}${key}/</div>`;
                result += userCacheFileManager.displayTree(tree[key], indent + '  ');
            }
        });
        return result;
    }

    userCacheFileManager.removeFile = function(fileName, sourcePath, status) {
        droppedFiles = droppedFiles.filter(item =>
            item && item.sourcePath && item.sourcePath !== sourcePath);
            userCacheFileManager.updateAssociatedFileTree();
            userCacheFileManager.updateFileCount();
    }

    userCacheFileManager.parseXnatUri = function(path, component) {
        const patterns = {
            project: /\/projects\/([^\/]+)/,
            subject: /\/subjects\/([^\/]+)/,
            experiment: /\/experiments\/([^\/]+)/,
            scan: /\/scans\/([^\/]+)/
        };

        if (!patterns[component]) {
            console.error('Invalid component: ' + component + '. Must be "project", "subject", or "experiment" or "scan."');
            return null;
        }
        const match = path.match(patterns[component]);
        return match ? match[1] : null;
    }

    userCacheFileManager.parseUri = function(path) {
        return {
            project: userCacheFileManager.parseXnatUri(path, 'project'),
            subject: userCacheFileManager.parseXnatUri(path, 'subject'),
            experiment: userCacheFileManager.parseXnatUri(path, 'experiment'),
            scan: userCacheFileManager.parseXnatUri(path, 'scan')
        };
    }

    userCacheFileManager.removeFileFromCache = function(filePath, absolutePath) {
        let urlTail = 'data/user/cache/resources/';
        let xnatFullPath = filePath.replace(CACHE_TREE_ROOT_NODE + '/', '');
        let partsArr = xnatFullPath.split('/');
        let resourceName = partsArr[0];
        urlTail += resourceName;
        let relativeFilePath = userCacheFileManager.getRelativePath(xnatFullPath);
        if (relativeFilePath !== '') {
            urlTail += '/files/' + relativeFilePath;
        }
        let deleteUrl = XNAT.url.csrfUrl(urlTail,{},false,false);

        XNAT.ui.dialog.open({
            title: 'Confirm Deletion',
            width: 350,
            content: '<p>Are you sure you want to permanently delete <strong>'+ xnatFullPath +'</strong>? This operation cannot be undone.</p>',
            buttons: [
                {
                    label: 'Confirm Delete',
                    isDefault: true,
                    close: false,
                    action: function(){
                        xmodal.loading.open({ title: 'Deleting element from cache...'});
                        XNAT.xhr.delete({
                            url: deleteUrl,
                            async: false,
                            success: function (data) {
                                XNAT.ui.banner.top(3000,'Successfully removed file ' + filePath + ' from cache.','success');
                                XNAT.app.userCacheFileManager.updateSourceTree(false);
                                XNAT.ui.dialog.closeAll();
                                xmodal.loading.close();
                            },
                            fail: function (e) {
                                XNAT.ui.banner.top(5000, 'Unable to remove the file ' + filePath + ' from the user cache.', 'error');
                                XNAT.ui.dialog.closeAll();
                                xmodal.loading.close();
                            }
                        });
                    }
                },
                {
                    label: 'Cancel',
                    close: true
                }
            ]
        })
    }

    userCacheFileManager.addNewResource = function(element) {
        console.log(userCacheFileManager.parseUri(element.getAttribute("data-uri")));
    }

    userCacheFileManager.addNewSubject = function(element) {
        const uriParts = userCacheFileManager.parseUri(element.getAttribute("data-uri"));
        window.create_subject_link = "$link.setPage('XDATScreen_edit_xnat_subjectData.vm').addPathInfo('popup','true')";
        const project = uriParts.project;
        if ((project == null) ) {
            xmodal.message('Create Subject', 'Please Add Yourself as a "Member" of the $displayManager.getSingularDisplayNameForProject().');
            return;
        }
        window.create_subject_link += "/project/" + project + "/destination/JS_Parent_Return.vm";
        this.subjectForm = popupCentered(window.create_subject_link,'Subject',610,800,10,'status=yes,resizable=yes,scrollbars=yes,toolbar=no');
        if (this.subjectForm.opener == null) this.subjectForm.opener = self;
        return this.subjectForm;
    }

    userCacheFileManager.addNewExperiment = function(element) {
        console.log(userCacheFileManager.parseUri(element.getAttribute("data-uri")));
    }

    userCacheFileManager.addNewScan = function(element) {
        console.log(userCacheFileManager.parseUri(element.getAttribute("data-uri")));
    }

    userCacheFileManager.getRelativePath = function(fullPath) {
        const parts = fullPath.split('/');
        if (parts.length <= 1) return '';
        return parts.slice(1).join('/');
    }

    userCacheFileManager.updateFileCount = function() {
        const totalFilesSpan = document.getElementById("totalFiles");
        totalFilesSpan.innerHTML = droppedFiles.length +  " files";
    }

    userCacheFileManager.deleteFolderAction = function(folderPath, folderData) {
        const parsedData = JSON.parse(folderData.replace(/&quot;/g, '"'));
        let fileCount = 0;
        if (parsedData.__files__) {
            fileCount += parsedData.__files__.length;
        }
        if (parsedData.files) {
            fileCount += parsedData.files.length;
        }

        const confirmMessage = fileCount > 0
        ? `Are you sure you want to delete folder "${folderPath}" and its ${fileCount} file(s)?`
        : `Are you sure you want to delete folder "${folderPath}"?`;

        if (confirm(confirmMessage)) {
            console.log('Deleting folder:', {
                path: folderPath,
                data: parsedData,
                fileCount: fileCount
            });

            // Add your folder deletion logic here:
            // - Remove from data structure
            // - Make API call to delete folder
            // - Update UI
            // - Remove from tree display

            alert(`Folder "${folderPath}" has been marked for deletion.`);

            // Optional: Remove from UI immediately
            // const folderElement = document.querySelector(`[data-folder-id="${folderPath}"]`);
            // if (folderElement) {
            //   folderElement.style.opacity = '0.5';
            //   folderElement.style.textDecoration = 'line-through';
            // }
        }
    }

    const reviewRow = document.getElementById('reviewRow');
    const actionRow = document.getElementById('actionRow');
    const reviewBtn = document.getElementById('reviewBtn');
    const addMoreBtn = document.getElementById('addMoreBtn');
    const ingestBtn = document.getElementById('ingestBtn');

    reviewBtn.addEventListener('click', function() {
        userCacheFileManager.updateAssociatedFileTree();
        reviewRow.classList.add('hidden');
        actionRow.classList.remove('hidden');
    });

    addMoreBtn.addEventListener('click', function() {
        actionRow.classList.add('hidden');
        reviewRow.classList.remove('hidden');
        userCacheFileManager.renderDestinationTree(destinationStructure);
        userCacheFileManager.hide(document.getElementById('associatedTree'));
        userCacheFileManager.show(document.getElementById('destinationTree'));

    });

    ingestBtn.addEventListener('click', function() {
        userCacheFileManager.ingest();
    });

    userCacheFileManager.ingest = function() {
        let populatedJsonArray = userCacheFileManager.populateFolderChildren(droppedFiles);
        console.log("To ingest " + JSON.stringify(populatedJsonArray));
        userCacheFileManager.submitToIngest(populatedJsonArray)
            .then(result => {
                console.log('Ingestion completed:', result);
            })
            .catch(error => {
                console.error('Ingestion failed:', error);
            });
    }

    userCacheFileManager.loadNode =  function(folderElement) {
        const nodeXnatType = folderElement.getAttribute('data-xnat-type');
        const nodeName = folderElement.getAttribute('data-name');
        const nodeUri = folderElement.getAttribute('data-uri');
        let isAlreadyLoaded = userCacheFileManager.isNodeLoaded(destinationStructure, nodeUri, );
        if (isAlreadyLoaded) {
            return;
        }
        switch(nodeXnatType) {
            case 'project':
                var resources = userCacheFileManager.fetchResourcesAtLevel(nodeUri, "project_resources");
                var subjects =  userCacheFileManager.fetchXnatDataAtLevel(nodeUri, 'subjects', '/subjects');
                userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendElementsToNode(nodeUri, resources, '/resources', 'Resources', nodeXnatType +'_resources', 'dropzone'));
                userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendElementsToNode(nodeUri, subjects, '/subjects', 'Subjects', 'subject', 'folder'));
                break;
            case 'subject':
                var resources = userCacheFileManager.fetchResourcesAtLevel(nodeUri, "subject_resources");
                var experiments =  userCacheFileManager.fetchXnatDataAtLevel(nodeUri, 'sessions', '/experiments');
                userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendElementsToNode(nodeUri, resources, '/resources', 'Resources', nodeXnatType +'_resources', 'dropzone'));
                userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendElementsToNode(nodeUri, experiments, '/experiments', 'Experiments', 'experiment', 'folder'));
                break;
            case 'experiment':
                var resources = userCacheFileManager.fetchResourcesAtLevel(nodeUri, "session_resources");
                var scans =  userCacheFileManager.fetchXnatDataAtLevel(nodeUri, 'scans', '/scans');
                userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendElementsToNode(nodeUri, resources, '/resources', 'Resources', nodeXnatType +'_resources', 'dropzone'));
                userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendElementsToNode(nodeUri, scans, '/scans', 'Scans', 'scan', 'folder'));
                break;
            case 'scan':
                var resources = userCacheFileManager.fetchResourcesAtLevel(nodeUri, "scan_resources");
                userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendElementsToNode(nodeUri, resources, '/resources', 'Resources', nodeXnatType +'_resources', 'dropzone'));
                break;
        }
    }

    userCacheFileManager.appendElementsToNode = function(parentNodeUri, elements, elementUri, elementName, elementXnatType, childType) {
        var fullUri =  parentNodeUri + elementUri;
        var fileTree = {name: elementName, type: "folder", xnatType: elementXnatType, uri: fullUri};
        fileTree.children = [];
        elements.forEach(element => {
            let label = (elementXnatType === "scan") ? element.ID : element.label;
            fileTree.children.push({name: label, type: childType, xnatType: elementXnatType, uri: fullUri + "/" + label});
        });
        return fileTree;
    }

    userCacheFileManager.findNodeByUri = function(data, targetUri) {
        if (!data) {
            return null;
        }

        if (Array.isArray(data)) {
            for (const item of data) {
                const result = userCacheFileManager.findNodeByUri(item, targetUri);
                if (result) {
                    return result;
                }
            }
            return null;
        }

        if (typeof data === 'object') {
            if (data.uri === targetUri) {
                return data;
            }

            if (data.children && Array.isArray(data.children)) {
                for (const child of data.children) {
                    const result = userCacheFileManager.findNodeByUri(child, targetUri);
                    if (result) {
                        return result;
                    }
                }
            }

            for (const key in data) {
                if (key !== 'uri' && key !== 'name' && key !== 'type' && key !== 'size' && key !== 'last') {
                    const result = userCacheFileManager.findNodeByUri(data[key], targetUri);
                    if (result) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
    * Adds a child to the node with the given URI
    * @param {Object|Array} data - The JSON data structure to search in
    * @param {string} targetUri - The URI to search for
    * @param {Object} childNode - The child node to add
    * @returns {boolean} - True if child was added successfully, false otherwise
    */
    userCacheFileManager.addChildToNode = function(data, targetUri, childNode) {
        const node = userCacheFileManager.findNodeByUri(data, targetUri);

        if (!node) {
            return false;
        }

        if (!node.children) {
            node.children = [];
        } else if (!Array.isArray(node.children)) {
            node.children = [];
        }
        node.children.push(childNode);
        return true;
    }

    userCacheFileManager.isNodeLoaded = function(data, targetUri) {
        const node = userCacheFileManager.findNodeByUri(data, targetUri);

        if (!node) {
            return true;
        }

        if (node.children && node.children.length>0) {
            return true;
        }
        return false;
    }

    /**
    * Finds a node and returns both the node reference and its parent for advanced modification
    * @param {Object|Array} data - The JSON data structure to search in
    * @param {string} targetUri - The URI to search for
    * @param {Object} parent - Internal parameter for tracking parent
    * @param {string|number} parentKey - Internal parameter for tracking parent key
    * @returns {Object|null} - Object with {node, parent, parentKey, path} or null if not found
    */
    userCacheFileManager.findNodeWithParent = function(data, targetUri, parent = null, parentKey = null, path = []) {
        if (!data) {
            return null;
        }

        if (Array.isArray(data)) {
            for (let i = 0; i < data.length; i++) {
                const result = userCacheFileManager.findNodeWithParent(data[i], targetUri, data, i, [...path, i]);
                if (result) {
                    return result;
                }
            }
            return null;
        }

        if (typeof data === 'object') {
            if (data.uri === targetUri) {
                return {
                    node: data,
                    parent: parent,
                    parentKey: parentKey,
                    path: path
                };
            }

            if (data.children && Array.isArray(data.children)) {
                for (let i = 0; i < data.children.length; i++) {
                    const result = userCacheFileManager.findNodeWithParent(data.children[i], targetUri, data.children, i, [...path, 'children', i]);
                    if (result) {
                        return result;
                    }
                }
            }

            for (const key in data) {
                if (key !== 'uri' && key !== 'name' && key !== 'type' && key !== 'size' && key !== 'last') {
                    const result = userCacheFileManager.findNodeWithParent(data[key], targetUri, data, key, [...path, key]);
                    if (result) {
                        return result;
                    }
                }
            }
        }
    return null;
    }

    /**
     * Removes a node with the given URI from its parent
     * @param {Object|Array} data - The JSON data structure to search in
     * @param {string} targetUri - The URI to search for
     * @returns {Object|null} - The removed node or null if not found
     */
    userCacheFileManager.removeNodeByUri = function(data, targetUri) {
        const result = userCacheFileManager.findNodeWithParent(data, targetUri);

        if (!result || !result.parent) {
            return null;
        }

        const { node, parent, parentKey } = result;

        if (Array.isArray(parent)) {
            const removedNode = parent.splice(parentKey, 1)[0];
            return removedNode;
        } else if (typeof parent === 'object') {
            const removedNode = parent[parentKey];
            delete parent[parentKey];
            return removedNode;
        }

        return null;
    }

    userCacheFileManager.init = async function() {
        userCacheFileManager.updateSourceTree(true);
        userCacheFileManager.updateDestinationTree();
    }

    userCacheFileManager.init();
    return XNAT.app.userCacheFileManager = userCacheFileManager;
}))