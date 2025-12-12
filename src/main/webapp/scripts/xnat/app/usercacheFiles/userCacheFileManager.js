/*
 * web: userCacheFileManager.js
 * XNAT http://www.xnat.org
 */

/*!
 * Facilitate user cache file manipulation
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

    // Sample source folder structure
    var sourceStructure;
    var destinationStructure;
    const CACHE_TREE_ROOT_NODE = "My Uploads";
    const ARCHIVE_TREE_ROOT_NODE = "Projects";
    const SCANS = "Scans";
    const SCANS_ROOT_NODE = "scans";
    const SCANS_NODE = "project_subject_experiment_scan";
    const SCANS_RESOURCE_NODE = "project_subject_experiment_scan_resource";
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
            return `${size.toFixed(1)} ${units[unit]}`;
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
            return `${path}/${node.name}`.replace(/^\//, '');
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
            //expandIcon.class = hasChildren ? (isExpanded ? '−' : '+') : '';

            if (hasChildren) {
                expandIcon.addEventListener('click', (e) => {
                    e.stopPropagation();
                    this.toggleExpand(nodeId);
                    this.render();
                });
            }

            // File/folder icon
            const icon = document.createElement('span');
            icon.className = isFolder ? 'folder-icon fa fa-folder' : 'file-icon fa fa-file';

            // Name
            const name = document.createElement('span');
            name.className = 'item-name';
            name.textContent = node.name;

            // Info (size, date, etc.)
            const info = document.createElement('span');
            info.className = `item-info ${isFolder ? '' : 'file-info'}`;

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

            // Add path info for detailed view
            if (node.destPath) {
                const pathDiv = document.createElement('div');
                pathDiv.className = 'tree-path';
                pathDiv.textContent = node.destPath || '';
                nodeDiv.appendChild(pathDiv);
            }

            // Children container
            if (hasChildren) {
                const childrenDiv = document.createElement('div');
                childrenDiv.className = `tree-children ${isExpanded ? 'expanded' : ''}`;

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

    //Upload Widget
    let isUploading = false;
    let isMinimized = false;

    // Status message system
    userCacheFileManager.showStatus = function(message, type = 'success') {
        const statusEl = document.getElementById('statusMessage');
        statusEl.textContent = message;
        statusEl.className = `status-message ${type} show`;
        setTimeout(() => {
            statusEl.classList.remove('show');
        }, 3000);
    }

    // File size formatter
    userCacheFileManager.formatFileSize = function(bytes) {
        if (bytes === 0) return '0 Bytes';
        const k = 1024;
        const sizes = ['Bytes', 'KB', 'MB', 'GB'];
        const i = Math.floor(Math.log(bytes) / Math.log(k));
        return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
    }

    // Generate XNAME (datestamp)
    userCacheFileManager.generateXName = function() {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const day = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const seconds = String(now.getSeconds()).padStart(2, '0');

        return `${year}${month}${day}-${hours}${minutes}${seconds}`;
    }

    // Update progress display
    userCacheFileManager.updateProgress = function(percentage, status) {
        const progressFill = document.getElementById('progressFill');
        const progressStatus = document.getElementById('progressStatus');
        const progressPercentage = document.getElementById('progressPercentage');

        progressFill.style.width = `${percentage}%`;
        progressStatus.textContent = status;
        progressPercentage.textContent = `${Math.round(percentage)}%`;
    }

    // Show/hide upload controls
    userCacheFileManager.toggleUploadControls = function(show) {
        const controls = document.getElementById('uploadControls');
        const progress = document.getElementById('progressContainer');

        if (show) {
            controls.classList.add('uce-show');
        } else {
            controls.classList.remove('uce-show');
            progress.classList.remove('uce-show');
        }
    }

    // Upload file to API
    userCacheFileManager.uploadFileToAPI = async function(file) {
        if (isUploading) return;

        isUploading = true;
        const uploadArea = document.querySelector('.uce-upload-area');
        const uploadBtn = document.getElementById('uploadBtn');
        const progressContainer = document.getElementById('progressContainer');

        // Update UI
        uploadArea.classList.add('uce-uploading');
        uploadBtn.disabled = true;
        progressContainer.classList.add('uce-show');
        userCacheFileManager.updateProgress(0, 'Preparing upload...');

        try {
            // Generate XNAME and construct endpoint
            const xname = userCacheFileManager.generateXName();
            const filename = encodeURIComponent(file.name);
            const endpoint = `${serverRoot}/data/user/cache/resources/${xname}/files/${filename}`;
            let uploadUrl = XNAT.url.csrfUrl(endpoint,{extract: true},false,false);
            // Use Fetch API with file in body
            const formData = new FormData();
            formData.append('file', file);
            const response = await fetch(uploadUrl, {
                method: 'PUT',
                body: formData
            });

            userCacheFileManager.updateProgress(90, 'Processing response...');

            if (!response.ok) {
                throw new Error(`Upload failed with status ${response.status}: ${response.statusText}`);
            }

            userCacheFileManager.updateProgress(100, 'Upload complete!');
            userCacheFileManager.showStatus(`File uploaded successfully to ${endpoint}`, 'success');
            userCacheFileManager.updateSourceTree();
            console.log('Upload of file ' + filename + ' to user cache successful');
            userCacheFileManager.resetUploadWidget();

        } catch (error) {
           console.error('Upload error:', error);
           userCacheFileManager.updateProgress(0, 'Upload failed');

           // Handle different error types
           let errorMessage = 'Upload failed';
           if (error.name === 'TypeError' && error.message.includes('fetch')) {
               errorMessage = 'Network error - check your connection';
           } else if (error.message.includes('status')) {
               errorMessage = error.message;
           } else {
               errorMessage = `Upload failed: ${error.message}`;
           }

           userCacheFileManager.showStatus(errorMessage, 'error');

           // Reset after delay
           setTimeout(() => {
               userCacheFileManager.resetUploadWidget();
           }, 3000);
        }
    }

    // Reset upload widget
    userCacheFileManager.resetUploadWidget = function() {
        isUploading = false;
        selectedZipFile = null;

        const uploadArea = document.querySelector('.uce-upload-area');
        const uploadBtn = document.getElementById('uploadBtn');
        const zipFileInput = document.getElementById('zipFile');

        uploadArea.classList.remove('uce-uploading');
        uploadBtn.disabled = false;
        zipFileInput.value = '';

        userCacheFileManager.toggleUploadControls(false);
    }

    // Toggle minimize state
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
            userCacheFileManager.showStatus('Upload widget minimized');
        } else {
            uploadContent.classList.remove('uce-minimized');
            uploadWidget.classList.remove('uce-minimized');
            minimizeBtn.innerHTML = '<i class="fa fa-minus-circle"></i>';
            minimizeBtn.title = 'Minimize';
            userCacheFileManager.showStatus('Upload widget maximized');
        }
    }

    // Update selected file display
    userCacheFileManager.updateSelectedFileDisplay = function(file) {
        const fileNameEl = document.getElementById('selectedFileName');
        const fileSizeEl = document.getElementById('selectedFileSize');

        fileNameEl.textContent = file.name;
        fileSizeEl.textContent = userCacheFileManager.formatFileSize(file.size);
        userCacheFileManager.toggleUploadControls(true);

        // Auto-expand if minimized when file is selected
        if (isMinimized) {
            userCacheFileManager.toggleMinimize();
            userCacheFileManager.showStatus(`File selected: ${file.name}. Widget expanded for upload.`);
        }
    }

    // ZIP file upload
    document.getElementById('zipFile').addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            selectedZipFile = file;
            userCacheFileManager.updateSelectedFileDisplay(file);
            userCacheFileManager.showStatus(`ZIP file "${file.name}" selected. Click upload to send to server.`);
        }
    });

    // Upload button click
    document.getElementById('uploadBtn').addEventListener('click', (e) => {
        e.stopPropagation();
        if (selectedZipFile && !isUploading) {
            userCacheFileManager.uploadFileToAPI(selectedZipFile);
        }
    });

    // Minimize button click
    document.getElementById('minimizeBtn').addEventListener('click', (e) => {
        e.stopPropagation();
        userCacheFileManager.toggleMinimize();
    });

    // Upload widget drag and drop
    const uploadArea = document.querySelector('.uce-upload-area');
    uploadArea.addEventListener('dragover', (e) => {
        e.preventDefault();
        uploadArea.classList.add('dragover');
    });

    uploadArea.addEventListener('dragleave', () => {
        uploadArea.classList.remove('dragover');
    });

    uploadArea.addEventListener('click', (e) => {
        // Prevent file dialog if widget is minimized
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
            userCacheFileManager.showStatus(`ZIP file "${selectedZipFile.name}" selected. Click upload to send to server.`);

            // Update file input
            const dt = new DataTransfer();
            dt.items.add(selectedZipFile);
            document.getElementById('zipFile').files = dt.files;
        } else {
            userCacheFileManager.showStatus('Please drop ZIP files only', 'error');
        }
    });

    //Left Panel
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
                errorHandler(e);
            }
        });
        return responseData;
    }

    // API functions (replace with actual API calls)
    userCacheFileManager.fetchProjects = async function() {
        if (userData['projects'].length == 0) {
          let response = userCacheFileManager.fetchData('/data/projects');
          userData['projects'].push(...response['ResultSet']['Result']);
          userCacheFileManager.sortAlphabetically(userData['projects']);
        }
        return userData['projects'] || [];
    }

    userCacheFileManager.fetchProjectResources =  function(projectUri) {
        if (!userData['project_resources'].hasOwnProperty(projectUri)) {
            let response = userCacheFileManager.fetchData( projectUri + '/resources');
            userData['project_resources'][projectUri] = [];
            userData['project_resources'][projectUri].push(...response['ResultSet']['Result']);
            userCacheFileManager.sortAlphabetically(userData['project_resources'][projectUri]);
        }
        return userData['project_resources'][projectUri] || [];
    }

    userCacheFileManager.sortAlphabetically = function(data) {
        data.sort((a, b) => {
            var nameA, nameB;
            try {
                nameA = a.name.toUpperCase(); // Ignore case for consistent sorting
                nameB = b.name.toUpperCase();
            } catch(error) {
                try {
                    nameA = a.label.toUpperCase(); // Ignore case for consistent sorting
                    nameB = b.label.toUpperCase();
                } catch(error1) {
                    nameA = a.ID.toUpperCase(); // Ignore case for consistent sorting
                    nameB = b.ID.toUpperCase();
                }
            }
            if (nameA < nameB) {
                return -1;
            }
            if (nameA > nameB) {
                return 1;
            }
            return 0; // names must be equal
        });
    }

    userCacheFileManager.fetchSubjects =   function(projectUri) {
        if (!userData['subjects'].hasOwnProperty(projectUri)) {
            let response =  userCacheFileManager.fetchData(projectUri + '/subjects');
            userData['subjects'][projectUri] = [];
            userData['subjects'][projectUri].push(...response['ResultSet']['Result']);
            userCacheFileManager.sortAlphabetically(userData['subjects'][projectUri]);
        }
        return userData['subjects'][projectUri] || [];
    }

    userCacheFileManager.fetchSubjectResources =   function(subjectUri) {
        if (!userData['subject_resources'].hasOwnProperty(subjectUri)) {
            let response = userCacheFileManager.fetchData(subjectUri + '/resources');
            userData['subject_resources'][subjectUri] = [];
            userData['subject_resources'][subjectUri].push(...response['ResultSet']['Result']);
            userCacheFileManager.sortAlphabetically(userData['subject_resources'][subjectUri]);
        }
        return userData['subject_resources'][subjectUri] || [];
    }

    userCacheFileManager.fetchSessions =  function(subjectUri) {
        if (!userData['sessions'].hasOwnProperty(subjectUri)) {
            let response = userCacheFileManager.fetchData(subjectUri + '/experiments');
            userData['sessions'][subjectUri] = [];
            userData['sessions'][subjectUri].push(...response['ResultSet']['Result']);
            userCacheFileManager.sortAlphabetically(userData['sessions'][subjectUri]);
        }
        return userData['sessions'][subjectUri] || [];
    }

    userCacheFileManager.fetchSessionResources =  function(experimentUri) {
        if (!userData['session_resources'].hasOwnProperty(experimentUri)) {
            let response = userCacheFileManager.fetchData(experimentUri + '/resources');
            userData['session_resources'][experimentUri] = [];
            userData['session_resources'][experimentUri].push(...response['ResultSet']['Result']);
            userCacheFileManager.sortAlphabetically(userData['session_resources'][experimentUri]);
        }
        return userData['session_resources'][experimentUri] || [];
    }

    userCacheFileManager.fetchScans =  function(experimentUri) {
        if (!userData['scans'].hasOwnProperty(experimentUri)) {
            let response = userCacheFileManager.fetchData(experimentUri + '/scans');
            userData['scans'][experimentUri] = [];
            userData['scans'][experimentUri].push(...response['ResultSet']['Result']);
            userCacheFileManager.sortAlphabetically(userData['session_resources'][experimentUri]);
        }
        return userData['scans'][experimentUri] || [];
    }

    userCacheFileManager.fetchScanResources =  function(scanUri) {
        if (!userData['scan_resources'].hasOwnProperty(scanUri)) {
            let response = userCacheFileManager.fetchData(scanUri +  '/resources');
            userData['scan_resources'][scanUri] = [];
            userData['scan_resources'][scanUri].push(...response['ResultSet']['Result']);
        }
        return userData['scan_resources'][scanUri] || [];
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
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const result = await response.json();
            console.log('Success:', result);
            return result;
        } catch (error) {
            console.error('Error submitting data:', error);
            throw error;
        }
    }

   // State management
    let expandedSourceFolders = new Set();
    let expandedDestinationFolders = new Set();
    let selectedPath = {};
    let selectLevels = ['project', 'project_resource', 'subject', 'subject_resource', 'session', 'session_resource', 'scan', 'scan_resource'];
    let droppedFiles = [];

    function getFileIcon(fileName) {
        const ext = fileName.split('.').pop().toLowerCase();
        const icons = {
            'txt': '<i class="fa  fa-file-text-o"></i>',
            'pdf': '<i class="fa  fa-file-pdf-o"></i>',
            'html': '<i class="fa fa-file-text-o"></i>',
            'css': '<i class="fa  fa-file-text-o"></i>',
            'js': '<i class="fa  fa-file-text-o"></i>',
            'jpg': '<i class="fa  fa-file-image-o"></i>',
            'png': '<i class="fa  fa-file-image-o"></i>',
            'gif': '<i class="fa  fa-file-image-o"></i>',
            'docx': '<i class="fa  fa-file-word-o"></i>',
            'pptx': '<i class="fa  fa-file-ppt-o"></i>',
            'xlsx': '<i class="fa  fa-file-xls-o"></i>',
            'json': '<i class="fa  fa-file-text-o"></i>',
            'zip': '<i class="fa  fa-file-zip-0"></i>',
            'exe': '<i class="fa  fa-file-code-o"></i>'
        };
        return icons[ext] || '<i class="fa  fa-file"></i>';
    }

    userCacheFileManager.renderSourceTree = function(node, enableDrag, path = "", level = 0) {
        let currentLevelDiv = spawn('div');
        let enableFileDragOptions = "";
        const folderPath = path + node.name;
        let includeDelete = enableDrag;
        if (enableDrag) {
            enableFileDragOptions =  'ondragstart="XNAT.app.userCacheFileManager.handleDragStart(event)" ondragend="XNAT.app.userCacheFileManager.handleDragEnd(event)"';
        }

        if (node.type === 'folder') {
            const isRootNode = userCacheFileManager.isRootNode(node.name);
            const isScansNode = node.xnatType === SCANS_NODE;
            const isScanLeafNode = node.nodeType === SCANS_RESOURCE_NODE;
            const isExpanded = (enableDrag ? expandedSourceFolders.has(path + node.name) : expandedDestinationFolders.has(path + node.name) ) || isRootNode || isScansNode || isScanLeafNode;
            const toggleIcon = isExpanded ? '<i class="fa fa-folder-open"></i>' : '<i class="fa fa-folder"></i>';

//            html += `
//                <div class="uce-folder-item ${isExpanded ? 'expanded' : ''}"
//                    data-path="${folderPath}"
//                    data-filename="${node.name}"
//                    data-type="${node.type}"
//                    dataAbsolutePath="${node.absolutePath}"
//                    ${draggable}
//                    ${enableFileDragOptions}>
//                     <span class="uce-folder-toggle" onclick="XNAT.app.userCacheFileManager.toggleFolder(event, '${folderPath}', ${enableDrag})">
//                          ${toggleIcon}
//                     </span>
//                     <span class="uce-folder-name">${node.name}</span>`;
            folderDiv = spawn('div', {'class': '"uce-folder-item" + isExpanded ? "expanded" : ""', 'data-path': folderPath,
                'data-filename': node.name, 'data-type': node.type, 'data-absolute-path': node.absolutePath,
                'draggable': enableDrag, 'enableFileDragOptions': enableFileDragOptions});
            folderDiv.append(spawn('span', {
                onclick: function (e) {
                    XNAT.app.userCacheFileManager.toggleFolder(event, folderPath, enableDrag);
                },
                'class': 'uce-folder-toggle',
                'html': toggleIcon
            }));
            folderDiv.append(spawn('span', {
                'class': 'uce-folder-name',
                'html': node.name
            }));

            if (!isRootNode && includeDelete) {
//                html +=  `<button onclick="XNAT.app.userCacheFileManager.removeFileFromCache('${folderPath}')"
//                    style="padding: 0; color: black; border: none;  cursor: pointer;"
//                    title="Figure out my data">
//                    <i class="fa fa-magic"></i>
//                </button>`;

                deleteButton =  spawn('button.btn.btn-sm.delete-cache-element', {
                    onclick: function (e) {
                        XNAT.app.userCacheFileManager.removeFileFromCache(folderPath)
                    },
                    title: "Delete from cache",
                    style: {color: 'black', border: 'none', cursor: 'pointer'}
                }, [spawn('i.fa.fa-trash')]);
                folderDiv.append(deleteButton);
//                `<button onclick="XNAT.app.userCacheFileManager.removeFileFromCache('${folderPath}')"
//                    style="padding: 0; color: black; border: none;  cursor: pointer;"
//                    title="Delete from cache">
//                    <i class="fa fa-trash"></i>
//                </button>`;
            }
            currentLevelDiv.append(folderDiv);

            if (isExpanded && node.children) {
                expandedDiv = spawn('div', {'class': 'uce-children'});
                node.children.forEach(child => {
                    expandedDiv.append(userCacheFileManager.renderSourceTree(child, enableDrag, path + node.name + '/', level + 1));
                });
                currentLevelDiv.append(expandedDiv);
            }
        } else  {
            fileDiv = spawn('div', {'class': "uce-file-item", 'data-path': path + node.name,
                'data-filename': node.name, 'data-type': node.type, 'data-absolute-path': node.absolutePath,
                'draggable': enableDrag, 'enableFileDragOptions': enableFileDragOptions});
            fileDiv.append(spawn('span', {
                'class': 'uce-icon',
                'html': getFileIcon(node.name)
            }));
            fileDiv.append(spawn('span', {
                'html': node.name
            }));
            if (includeDelete) {
//                html += `<button onclick="XNAT.app.userCacheFileManager.removeFileFromCache('${folderPath}')"
//                    style="padding:0; color: black; border: none; cursor: pointer;"
//                    title="Delete from cache">
//                    <i class="fa fa-trash"></i>
//                    </button>`;
                deleteButton =  spawn('button.btn.btn-sm.delete-cache-element', {
                    onclick: function (e) {
                        XNAT.app.userCacheFileManager.removeFileFromCache(folderPath)
                    },
                    title: "Delete from cache",
                    style: {color: 'black', border: 'none', cursor: 'pointer'}
                }, [spawn('i.fa.fa-trash')]);
                fileDiv.append(deleteButton);
            }
//            html += `
//                <div class="uce-file-item"
//                    ${draggable}
//                    data-filename="${node.name}"
//                    data-path="${path + node.name}"
//                    data-type="${node.type}"
//                    dataAbsolutePath="${node.absolutePath}"
//                    ${enableFileDragOptions}
//                    >
//                    <span class="uce-icon">${getFileIcon(node.name)}</span>
//                    <span>${node.name}</span>`;
//                    if (includeDelete) {
//                        html += `<button onclick="XNAT.app.userCacheFileManager.removeFileFromCache('${folderPath}')"
//                            style="padding:0; color: black; border: none; cursor: pointer;"
//                            title="Delete from cache">
//                            <i class="fa fa-trash"></i>
//                            </button>`;
//                    }
//                html += `</div>`;
            currentLevelDiv.append(fileDiv);
        }
        return currentLevelDiv;
    }

    userCacheFileManager.isRootNode = function(nodeName) {
        return nodeName === CACHE_TREE_ROOT_NODE || nodeName === ARCHIVE_TREE_ROOT_NODE;
    }

    userCacheFileManager.isScansRootNode = function(node) {
        return  node.xnatType === SCANS_ROOT_NODE ;
    }

    userCacheFileManager.isScansResourceNode = function(node) {
        return  node.xnatType === SCANS_ROOT_NODE ;
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

    userCacheFileManager.toggleSourceFolder = function(folderPath) {
        if (expandedSourceFolders.has(folderPath)) {
            expandedSourceFolders.delete(folderPath);
        } else {
            expandedSourceFolders.add(folderPath);
        }
        userCacheFileManager.updateSourceTree();
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
                errorHandler(e);
            }
        });
    }

    userCacheFileManager.updateSourceTree = function() {
        userCacheFileManager.fetchUserCacheFiles();
        const treeContainer = document.getElementById('sourceTree');
        treeContainer.append(userCacheFileManager.renderSourceTree(sourceStructure, true));
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
        e.target.setAttribute("draggable", false);
        e.target.classList.remove('dragging');
    }

    // Drop zone handlers
    userCacheFileManager.setupDropZone = function() {
        const dropZoneDivs = document.querySelectorAll('.uce-drop-zone');

        // Iterate through the found elements
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
            dropZoneDiv.addEventListener('mouseenter', () => {
                const tooltip = dropZoneDiv.querySelector('.uce-drop-zone-tooltip');
                tooltip.style.display = 'block'; // Show the tooltip
            });
            dropZoneDiv.addEventListener('mouseleave', () => {
                const tooltip = dropZoneDiv.querySelector('.uce-drop-zone-tooltip');
                tooltip.style.display = 'none'; // Hide the tooltip
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
        droppedFiles.push(fileInfo);
        userCacheFileManager.updateFileCount();
        userCacheFileManager.showStatus(`${fileData.name} associated to ${destinationPath}`, 'success');
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
            const result = userCacheFileManager.searchNodeByAbsolutePath (sourceStructure, absolutePath);
            if (result) {
                return result;
            }
        }
        return [];
    }

    userCacheFileManager.searchNodeByAbsolutePath = function(node, targetAbsolutePath) {
        // Check if current node matches the target path
        if (node.absolutePath === targetAbsolutePath) {
            return node.children || [];
        }

        // If this node has children, search through them
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
        // Split by path separator
        const pathParts = cleanPath.split('/');
        // Return the last part (which is the parent folder name)
        return pathParts[pathParts.length - 1];
    }

    userCacheFileManager.populateFolderChildren = function(jsonArray) {
        let updatedJsonArray = [];
        jsonArray.forEach(item => {
            if (item.type === 'folder') {
                // Look up children from sourceStructure using absolutePath
                if (item.absolutePath && sourceStructure) {
                    const folderChildren = userCacheFileManager.findChildrenByAbsolutePath(item.absolutePath);
                    const parentFolderName = userCacheFileManager.getParentFolderName(item.absolutePath);
                    if (folderChildren && Array.isArray(folderChildren) && folderChildren.length > 0) {
                        // Process each child
                        folderChildren.forEach(child => {
                            // Create the child's full destination path
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

    userCacheFileManager.convertToTree2 = function(jsonArray) {
        const tree = {};

        jsonArray.forEach(item => {
            // Split the destPath into segments
            const pathSegments = item.destPath.split('/');
            let currentNode = tree;

            // Navigate through each path segment
            pathSegments.forEach((segment, index) => {
                // If this segment doesn't exist, create it
                if (!currentNode[segment]) {
                    currentNode[segment] = {
                        type: 'folder',
                        children: {},
                        files: []
                    };
                }

                // If this is the last segment
                if (index === pathSegments.length - 1) {
                    if (item.type === 'folder') {
                        // For folders, ensure the node exists and update its properties
                        currentNode[segment].name = item.name;
                        currentNode[segment].type = item.type;
                        currentNode[segment].sourcePath = item.sourcePath;
                        currentNode[segment].destPath = item.destPath;
                        currentNode[segment].status = item.status;
                        currentNode[segment].absolutePath = item.absolutePath;


                        // Look up children from sourceStructure using absolutePath
                        if (item.absolutePath && sourceStructure) {
                            const folderChildren = userCacheFileManager.findChildrenByAbsolutePath(item.absolutePath);
                            const parentFolderName = userCacheFileManager.getParentFolderName(item.absolutePath);
                            if (folderChildren && Array.isArray(folderChildren) && folderChildren.length > 0) {
                                // Process each child
                                folderChildren.forEach(child => {
                                    // Create the child's full destination path
                                    const childDestPath = item.destPath + '/' + parentFolderName + '/' + child.name;
                                    const childItem = {
                                        ...child,
                                        destPath: childDestPath
                                    };

                                    // Add child to the array for processing
                                    jsonArray.push(childItem);
                                });
                            }
                        }
                    } else {
                        // For files, add to the files array
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
                    // Move to the next level
                    currentNode = currentNode[segment].children;
                }
            });
        });
        return tree;
    }

    userCacheFileManager.convertToTree1 = function(jsonArray) {
        const tree = {};
        jsonArray.forEach(item => {
            // Split the destPath into segments
            const pathSegments = item.destPath.split('/');
            let currentNode = tree;

            // Navigate through each path segment
            pathSegments.forEach((segment, index) => {
                // If this segment doesn't exist, create it
                if (!currentNode[segment]) {
                    currentNode[segment] = {
                    type: 'folder',
                    children: {},
                    files: []
                    };
                }

                // If this is the last segment, add the file here
                if (index === pathSegments.length - 1) {
                    currentNode[segment].files.push({
                        name: item.name,
                        type: item.type,
                        sourcePath: item.sourcePath,
                        destPath: item.destPath,
                        status: item.status
                    });
                } else {
                    // Move to the next level
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
            console.error(`Invalid component: ${component}. Must be 'project', 'subject', or 'experiment' or 'scan'`);
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

    userCacheFileManager.removeFileFromCache = function(filePath) {
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
                    close: true,
                    action: function(){
                        userCacheFileManager.deleteCacheFile(deleteUrl);
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

    userCacheFileManager.deleteCacheFile = function(deleteUrl) {
        XNAT.xhr.delete({
            url: deleteUrl,
            async: false,
            success: function (data) {
                XNAT.app.userCacheFileManager.updateSourceTree();
            },
            fail: function (e) {
                errorHandler(e);
            }
        });
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

    userCacheFileManager.toggleFolder = function(event, folderPath, isSourcePane) {
        event.preventDefault();
        const folderElement = event.currentTarget;
        const isExpanded = folderElement.getAttribute('data-expanded') === 'true';

        if (isExpanded) {
            userCacheFileManager.collapseFolder(folderElement, folderPath);
        } else {
            userCacheFileManager.expandFolder(folderElement, folderPath, isSourcePane);
        }
    }

    userCacheFileManager.expandFolder = function(folderElement, folderPath, isSourcePane) {
        folderElement.setAttribute('data-expanded', 'true');
        if (isSourcePane) {
            if (expandedSourceFolders.has(folderPath)) {
                expandedSourceFolders.delete(folderPath);
            } else {
                expandedSourceFolders.add(folderPath);
            }
            $('#sourceTree.className:first-child').replaceWith(userCacheFileManager.renderSourceTree(sourceStructure, true));
        }
        folderElement.style.backgroundColor = '#e8f4fd';
    }

    userCacheFileManager.collapseFolder = function(folderElement, folderPath) {
        folderElement.setAttribute('data-expanded', 'false');
        folderElement.style.backgroundColor = '';
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

    /*********************************************
    / Code for Destination panel */

    const reviewRow = document.getElementById('reviewRow');
    const actionRow = document.getElementById('actionRow');
    const reviewBtn = document.getElementById('reviewBtn');
    const addMoreBtn = document.getElementById('continueBtn');
    const ingestBtn = document.getElementById('ingestBtn');

    // Review button click handler
    reviewBtn.addEventListener('click', function() {
        userCacheFileManager.updateAssociatedFileTree();
        reviewRow.classList.add('hidden');
        actionRow.classList.remove('hidden');
    });

    // Add More button click handler
    continueBtn.addEventListener('click', function() {
        actionRow.classList.add('hidden');
        reviewRow.classList.remove('hidden');
        userCacheFileManager.renderDestinationTree(destinationStructure);
        userCacheFileManager.hide(document.getElementById('associatedTree'));
        userCacheFileManager.show(document.getElementById('destinationTree'));

    });

    // Ingest button click handler
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

    // Cache for loaded data
    userCacheFileManager.updateDestinationTree = async function() {
        var projects = await userCacheFileManager.fetchProjects();
        const treeContainer = document.getElementById('destinationTree');
        destinationStructure = userCacheFileManager.convertXnatUserDataToFileTree();
        treeContainer.innerHTML = userCacheFileManager.renderDestinationTree(destinationStructure);
    }

    userCacheFileManager.convertXnatUserDataToFileTree =  function() {
        var fileTree = {name: ARCHIVE_TREE_ROOT_NODE, type: "folder", xnatType: "archive", uri: ""};
        fileTree.children = [];
        userData['projects'].forEach(project => {
            fileTree.children.push({name: project.name, type: "folder", xnatType: "project", uri: project.URI});
        });
        return fileTree;
    }

    userCacheFileManager.renderDestinationTree = function(node, path = "", level = 0) {
        let html = '';
        const folderPath = path + node.name;

        if (node.type === 'folder') {
            const isRootNode = userCacheFileManager.isRootNode(node.name);
            const isExpanded = expandedDestinationFolders.has(path + node.name)  || isRootNode ||  userCacheFileManager.isScansRootNode(node);
            const toggleIcon = isExpanded ?  '<i class="fa fa-minus"></i>'  : '<i class="fa fa-plus"></i>';

            html += `
            <div class="uce-destination-folder-item ${isExpanded ? 'expanded' : ''}"
                data-path="${folderPath}" data-uri="${node.uri}" data-name="${node.name}" data-xnatType="${node.xnatType}"
                 <span class="uce-folder-toggle" onclick="XNAT.app.userCacheFileManager.toggleDestinationFolder(event, '${folderPath}')">
                      ${toggleIcon}
                 </span>
                 <span class="uce-folder-name">${node.name}</span>`;
            if (node.xnatType === 'resources') {
                html +=  `<button onclick="XNAT.app.userCacheFileManager.addNewResource(this)" data-uri="${node.uri}"
                    style="padding: 0; color: black; border: none;  cursor: pointer;"
                    title="Add Resource">
                    <i class="fa fa-folder"></i>
                    </button>`;
            } else if (node.xnatType === 'subjects') {
                html +=  `<button onclick="XNAT.app.userCacheFileManager.addNewSubject(this)" data-uri="${node.uri}"
                    style="padding: 0; color: black; border: none;  cursor: pointer;"
                    title="Add Subject">
                    <i class="fa fa-user-plus"></i>
                    </button>`;
            }  else if (node.xnatType === 'experiments') {
                html +=  `<button onclick="XNAT.app.userCacheFileManager.addNewExperiment(this)" data-uri="${node.uri}"
                    style="padding: 0; color: black; border: none;  cursor: pointer;"
                    title="Add Experiment">
                    <i class="fa fa-flask" ></i>
                    </button>`;
            }  else if (node.xnatType === 'scans') {
                html +=  `<button onclick="XNAT.app.userCacheFileManager.addNewScan(this)" data-uri="${node.uri}"
                    style="padding: 0; color: black; border: none;  cursor: pointer;"
                    title="Add Scan">
                    <i class="fa fa-search" ></i>
                    </button>`;
            }

            html += `</div>`;
            if (isExpanded && node.children) {
                html += '<div class="uce-children">';
                node.children.forEach(child => {
                    html += userCacheFileManager.renderDestinationTree(child, path + node.name + '/', level + 1);
                });
                html += '</div>';
            }
        } else {
            html += `
                <div class="uce-drop-zone"
                data-filename="${node.name}"
                data-path="${path + node.name}" data-uri="${node.uri}"
                >
                <span class="uce-dropbox-icon"><i class="fa fa-dropbox"></i></span>
                <span class="uce-drop-zone-tooltip" style="display:none">${node.uri}</span>
                <br>
                <span class="uce-dropbox-icon">Drop files here for ${node.name}</span>
                </div>`;
        }
        return html;
    }

    userCacheFileManager.toggleDestinationFolder = function(event, folderPath) {
        event.preventDefault();
        const folderElement = event.currentTarget;
        const isExpanded = folderElement.getAttribute('data-expanded') === 'true';

        if (isExpanded) {
            userCacheFileManager.collapseDestinationFolder(folderElement, folderPath);
        } else {
            userCacheFileManager.expandDestinationFolder(folderElement, folderPath);
        }
    }

    userCacheFileManager.expandDestinationFolder = function(folderElement, folderPath) {
        folderElement.setAttribute('data-expanded', 'true');
        if (expandedDestinationFolders.has(folderPath)) {
            expandedDestinationFolders.delete(folderPath);
        } else {
            userCacheFileManager.loadNode(folderElement);
            expandedDestinationFolders.add(folderPath);
        }
        const treeContainer = document.getElementById('destinationTree');
        treeContainer.innerHTML = userCacheFileManager.renderDestinationTree(destinationStructure);
        userCacheFileManager.setupDropZone();
        folderElement.style.backgroundColor = '#e8f4fd';
    }

    userCacheFileManager.collapseDestinationFolder = function(folderElement, folderPath) {
        folderElement.setAttribute('data-expanded', 'false');
        folderElement.style.backgroundColor = '';
    }

    userCacheFileManager.loadNode =  function(folderElement) {
        //Based on the type of the node, get the data
        //Update the destinationStructure and render
        const nodeXnatType = folderElement.getAttribute('data-xnatType');
        const nodeName = folderElement.getAttribute('data-name');
        const nodeUri = folderElement.getAttribute('data-uri');
        let isAlreadyLoaded = userCacheFileManager.isNodeLoaded(destinationStructure, nodeUri, );
        if (isAlreadyLoaded) {
            return;
        }
        switch(nodeXnatType) {
            case 'project':
            var resources = userCacheFileManager.fetchProjectResources(nodeUri);
            var subjects =  userCacheFileManager.fetchSubjects(nodeUri);
            userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendResourcesToNode(nodeUri, resources, nodeXnatType));
            userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendSubjectsToNode(nodeUri, subjects));
            break;
            case 'project_subject':
            var resources = userCacheFileManager.fetchSubjectResources(nodeUri);
            var experiments =  userCacheFileManager.fetchSessions(nodeUri);
            userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendResourcesToNode(nodeUri, resources, nodeXnatType));
            userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendSessionsToNode(nodeUri, experiments));
            break;
            case 'project_subject_experiment':
            var resources = userCacheFileManager.fetchSessionResources(nodeUri);
            var scans =  userCacheFileManager.fetchScans(nodeUri);
            userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendResourcesToNode(nodeUri, resources, nodeXnatType));
            userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendScansToNode(nodeUri, scans));
            break;
            case 'project_subject_experiment_scan':
            var resources = userCacheFileManager.fetchScanResources(nodeUri);
            userCacheFileManager.addChildToNode(destinationStructure, nodeUri, userCacheFileManager.appendResourcesToNode(nodeUri, resources, nodeXnatType));
            break;
        }
    }

    userCacheFileManager.appendResourcesToNode = function(parentNodeUri, resources, nodeXnatType) {
        var resourcesUri =  parentNodeUri + "/resources";
        var fileTree = {name: "Resources", type: "folder", xnatType: "resources", uri: resourcesUri};
        fileTree.children = [];
        resources.forEach(resource => {
            fileTree.children.push({name: resource.label, type: "dropzone", xnatType: nodeXnatType + "_resource", uri: resourcesUri + "/" + resource.label});
        });
        return fileTree;
    }

    userCacheFileManager.appendSubjectsToNode = function(parentNodeUri, subjects) {
        var subjectsUri =  parentNodeUri + "/subjects";
        var fileTree = {name: "Subjects", type: "folder", xnatType: "subjects", uri: subjectsUri};
        fileTree.children = [];
        subjects.forEach(subject => {
            fileTree.children.push({name: subject.label, type: "folder", xnatType: "project_subject", uri: subjectsUri + "/" + subject.label});
        });
        return fileTree;
    }

     userCacheFileManager.appendSessionsToNode = function(parentNodeUri, experiments) {
           var experimentsUri =  parentNodeUri + "/experiments";
           var fileTree = {name: "Experiments", type: "folder", xnatType: "experiments", uri: experimentsUri};
           fileTree.children = [];
           experiments.forEach(exp => {
                fileTree.children.push({name: exp.label, type: "folder", xnatType: "project_subject_experiment", uri: experimentsUri + "/" + exp.label});
            });
            return fileTree;
        }

    userCacheFileManager.appendScansToNode = function(parentNodeUri, scans) {
        var scansUri =  parentNodeUri + "/scans";
        var fileTree = {name: SCANS, type: "folder", xnatType: SCANS_ROOT_NODE, uri: scansUri};
        fileTree.children = [];
        scans.forEach(scan => {
            fileTree.children.push({name: scan.ID, type: "folder", xnatType: "project_subject_experiment_scan", uri: scansUri + "/" + scan.ID});
        });
        return fileTree;
    }

    userCacheFileManager.modifyNodeByUri = function (data, targetUri, modifications) {
        const node = userCacheFileManager.findNodeByUri(data, targetUri);

        if (!node) {
            return false;
        }

        if (typeof modifications === 'function') {
            // If modifications is a function, call it with the node
            modifications(node);
        } else if (typeof modifications === 'object' && modifications !== null) {
            // If modifications is an object, merge properties
            Object.assign(node, modifications);
        }
        return true;
    }

    userCacheFileManager.findNodeByUri = function(data, targetUri) {
        // Handle null or undefined data
        if (!data) {
            return null;
        }

        // If data is an array, search through each element
        if (Array.isArray(data)) {
            for (const item of data) {
                const result = userCacheFileManager.findNodeByUri(item, targetUri);
                if (result) {
                    return result;
                }
            }
            return null;
        }

        // If data is an object
        if (typeof data === 'object') {
            // Check if this node has the matching URI
            if (data.uri === targetUri) {
                return data; // Return reference to original object
            }

            // Search in children array if it exists
            if (data.children && Array.isArray(data.children)) {
                for (const child of data.children) {
                    const result = userCacheFileManager.findNodeByUri(child, targetUri);
                    if (result) {
                        return result;
                    }
                }
            }

            // Search in all other properties that might contain nested objects or arrays
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

        // Ensure children array exists
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
        // Handle null or undefined data
        if (!data) {
            return null;
        }

        // If data is an array, search through each element
        if (Array.isArray(data)) {
            for (let i = 0; i < data.length; i++) {
                const result = userCacheFileManager.findNodeWithParent(data[i], targetUri, data, i, [...path, i]);
                if (result) {
                    return result;
                }
            }
            return null;
        }

        // If data is an object
        if (typeof data === 'object') {
            // Check if this node has the matching URI
            if (data.uri === targetUri) {
                return {
                    node: data,
                    parent: parent,
                    parentKey: parentKey,
                    path: path
                };
            }

            // Search in children array if it exists
            if (data.children && Array.isArray(data.children)) {
                for (let i = 0; i < data.children.length; i++) {
                    const result = userCacheFileManager.findNodeWithParent(data.children[i], targetUri, data.children, i, [...path, 'children', i]);
                    if (result) {
                        return result;
                    }
                }
            }

            // Search in all other properties that might contain nested objects or arrays
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
            // Remove from array
            const removedNode = parent.splice(parentKey, 1)[0];
            return removedNode;
        } else if (typeof parent === 'object') {
            // Remove from object
            const removedNode = parent[parentKey];
            delete parent[parentKey];
            return removedNode;
        }

        return null;
    }

    /**
     * Replaces a node with the given URI with a new node
     * @param {Object|Array} data - The JSON data structure to search in
     * @param {string} targetUri - The URI to search for
     * @param {Object} newNode - The new node to replace with
     * @returns {Object|null} - The old node that was replaced or null if not found
     */
    userCacheFileManager.replaceNodeByUri = function(data, targetUri, newNode) {
        const result = userCacheFileManager.findNodeWithParent(data, targetUri);

        if (!result || !result.parent) {
            return null;
        }

        const { node, parent, parentKey } = result;
        const oldNode = { ...node }; // Create a copy of the old node

        if (Array.isArray(parent)) {
            parent[parentKey] = newNode;
        } else if (typeof parent === 'object') {
            parent[parentKey] = newNode;
        }

        return oldNode;
    }


    // Initialize
    userCacheFileManager.init = async function() {
        userCacheFileManager.updateSourceTree();
        userCacheFileManager.updateDestinationTree();
    }

    // Start the application
    userCacheFileManager.init();

    return XNAT.app.userCacheFileManager = userCacheFileManager;

}))