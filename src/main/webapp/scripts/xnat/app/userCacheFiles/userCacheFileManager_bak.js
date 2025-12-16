/*
 * web: usercacheFileManager.js
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

    var usercacheFileManager;
    XNAT.app = getObject(XNAT.app || {});

    XNAT.app.usercacheFileManager = usercacheFileManager =
        getObject(XNAT.app.usercacheFileManager || {});

       // Sample source folder structure
            var sourceStructure;

            // Sample destination folder structure
            const destinationStructure = {
                name: "XNAT",
                type: "folder",
                children: [
                    {
                        name: "Project1",
                        type: "folder",
                        children: [
                            {
                                name: "Subject1",
                                type: "folder",
                                children: [
                                    {
    				   name: "Session1", type: "folder",
                                       children: [
    					  { name: "RESOURCES", type: "folder", children: [] },
    					  {
    					    name: "SCANS", type: "folder",
    					    children: [
    						{ name: "SCAN_1", type: "folder",
    						  children: [
    						     { name: "RESOURCES", type: "folder", children: [] }
    						   ]
    						},
    						{ name: "SCAN_2", type: "folder",
                                                      children: [
                                                         { name: "RESOURCES", type: "folder", children: [] }
    					          ]
                                                    }
    				            ]
                                              }
                                       ]
                                   }
                              ]
                           }
                    ]
                }
             ]
           };

            let droppedFiles = [];
            let expandedSourceFolders = new Set();
            let expandedDestFolders = new Set();

            function getFileIcon(fileName) {
                const ext = fileName.split('.').pop().toLowerCase();
                const icons = {
                    'txt': '<i class="fa-solid fa-file-lines"></i>',
                    'pdf': '<i class="fa-solid fa-file-pdf"></i>',
                    'html': '<i class="fa-solid fa-file-lines"></i>',
                    'css': '<i class="fa-solid fa-file-lines"></i>',
                    'js': '<i class="fa-solid fa-file-lines"></i>',
                    'jpg': '<i class="fa-solid fa-file-image"></i>',
                    'png': '<i class="fa-solid fa-file-image"></i>',
                    'gif': '<i class="fa-solid fa-file-image"></i>',
                    'docx': '<i class="fa-solid fa-file-word"></i>',
                    'pptx': '<i class="fa-solid fa-file-ppt"></i>',
                    'xlsx': '<i class="fa-solid fa-file-xls"></i>',
                    'json': '<i class="fa-solid fa-file-lines"></i>',
                    'zip': '<i class="fa-solid fa-file-zip"></i>',
                    'exe': '<i class="fa-solid fa-file-code"></i>'
                };
                return icons[ext] || '<i class="fa-solid fa-file"></i>';
            }

            usercacheFileManager.renderSourceTree = function(node, path = "", level = 0) {
                let html = '';

                if (node.type === 'folder') {
                    const isExpanded = expandedSourceFolders.has(path + node.name);
                    const toggleIcon = isExpanded ? '<i class="fa-solid fa-caret-down"></i>' : '<i class="fa-solid fa-caret-right"></i>';

                    html += `
                        <div class="folder-item ${isExpanded ? 'expanded' : ''}"
                             data-path="${path + node.name}"
                             onclick="XNAT.app.usercacheFileManager.toggleSourceFolder('${path + node.name}')">
                            <span class="expand-toggle ${isExpanded ? 'expanded' : ''}">${toggleIcon}</span>
                            <span class="icon"><i class="fa-solid fa-folder-plus"></i></span>
                            <span>${node.name}</span>
                        </div>
                    `;

                    if (isExpanded && node.children) {
                        html += '<div class="children">';
                        node.children.forEach(child => {
                            html += XNAT.app.usercacheFileManager.renderSourceTree(child, path + node.name + '/', level + 1);
                        });
                        html += '</div>';
                    }
                } else {
                    html += `
                        <div class="file-item"
                             draggable="true"
                             data-filename="${node.name}"
                             data-path="${path + node.name}"
                             ondragstart="XNAT.app.usercacheFileManager.handleDragStart(event)"
                             ondragend="XNAT.app.usercacheFileManager.handleDragEnd(event)">
                            <span class="icon">${getFileIcon(node.name)}</span>
                            <span>${node.name}</span>
                        </div>
                    `;
                }

                return html;
            }

            usercacheFileManager.renderDestinationTree = function(node, path = "", level = 0) {
                let html = '';

                if (node.type === 'folder') {
                    const isExpanded = expandedDestFolders.has(path + node.name);
                    const toggleIcon = isExpanded ? '▼' : '▶';
                    const fullPath = path + node.name;
                    const nodeId = `dest-${fullPath.replace(/[^a-zA-Z0-9]/g, '-')}`;

                    html += `
                        <div class="folder-item destination-folder ${isExpanded ? 'expanded' : ''}"
                             data-path="${fullPath}"
                             data-node-id="${nodeId}"
                             onclick="XNAT.app.usercacheFileManager.toggleDestFolder('${fullPath}')"
                             oncontextmenu="XNAT.app.usercacheFileManager.showContextMenu(event, '${fullPath}')"
                             ondragover="XNAT.app.usercacheFileManager.handleDestDragOver(event)"
                             ondragleave="XNAT.app.usercacheFileManager.handleDestDragLeave(event)"
                             ondrop="XNAT.app.usercacheFileManager.handleDestDrop(event, '${fullPath}')">
                            <span class="expand-toggle ${isExpanded ? 'expanded' : ''}" onclick="event.stopPropagation(); XNAT.app.usercacheFileManager.toggleDestFolder('${fullPath}')">${toggleIcon}</span>
                            <span class="icon"><i class="fa-solid fa-folder-plus"></i></span>
                            <span class="folder-name" id="name-${nodeId}" ondblclick="event.stopPropagation(); XNAT.app.usercacheFileManager.startEdit('${fullPath}', '${nodeId}')">${node.name}</span>
                            <span class="file-count">(${node.children.length})</span>
                            <button class="edit-button" onclick="event.stopPropagation(); XNAT.app.usercacheFileManager.startEdit('${fullPath}', '${nodeId}')"><i class="fa-solid fa-pen"></i></button>
                        </div>
                    `;

                    if (isExpanded && node.children) {
                        html += '<div class="children">';
                        node.children.forEach(child => {
                            html += XNAT.app.usercacheFileManager.renderDestinationTree(child, path + node.name + '/', level + 1);
                        });
                        html += '</div>';
                    }
                } else {
                    // Render files in destination
                    html += `
                        <div class="file-item destination-file">
                            <span class="icon">${getFileIcon(node.name)}</span>
                            <span>${node.name}</span>
                            <span style="margin-left: auto; font-size: 12px; color: #666;">${node.uploadTime || ''}</span>
                        </div>
                    `;
                }

                return html;
            }

            usercacheFileManager.toggleSourceFolder = function (folderPath) {
                if (expandedSourceFolders.has(folderPath)) {
                    expandedSourceFolders.delete(folderPath);
                } else {
                    expandedSourceFolders.add(folderPath);
                }
                XNAT.app.usercacheFileManager.updateSourceTree();
            }

            usercacheFileManager.toggleDestFolder = function(folderPath) {
                if (expandedDestFolders.has(folderPath)) {
                    expandedDestFolders.delete(folderPath);
                } else {
                    expandedDestFolders.add(folderPath);
                }
               XNAT.app.usercacheFileManager.updateDestinationTree();
            }

            function fetchUserCacheFiles() {
              let usercacheFileUrl = XNAT.url.restUrl('data/user/cache/resources?format=treeJson',{},false,false);
              XNAT.xhr.get({
                          url: usercacheFileUrl,
                          async: false,
                          success: function (data) {
                              sourceStructure = data;
                          },
                          fail: function (e) {
                              errorHandler(e);
                          }
                      });
            }

            usercacheFileManager.updateSourceTree = function() {
                fetchUserCacheFiles();
                const treeContainer = document.getElementById('sourceTree');
                treeContainer.innerHTML = XNAT.app.usercacheFileManager.renderSourceTree(sourceStructure);
            }

            usercacheFileManager.updateDestinationTree = function() {
                const treeContainer = document.getElementById('destinationTree');
                treeContainer.innerHTML = XNAT.app.usercacheFileManager.renderDestinationTree(destinationStructure);
            }

            usercacheFileManager.handleDragStart = function(e) {
                const fileName = e.target.dataset.filename;
                const filePath = e.target.dataset.path;

                e.dataTransfer.setData('text/plain', JSON.stringify({
                    name: fileName,
                    path: filePath
                }));

                e.target.classList.add('dragging');
            }

            usercacheFileManager.handleDragEnd = function(e) {
                e.target.classList.remove('dragging');
            }

            // Destination tree drag and drop handlers
            usercacheFileManager.handleDestDragOver = function(e) {
                e.preventDefault();
                e.stopPropagation();
                e.currentTarget.classList.add('drag-over');
            }

            usercacheFileManager.handleDestDragLeave = function(e) {
                e.preventDefault();
                e.stopPropagation();
                e.currentTarget.classList.remove('drag-over');
            }

            usercacheFileManager.handleDestDrop = function(e, destPath) {
                e.preventDefault();
                e.stopPropagation();
                e.currentTarget.classList.remove('drag-over');

                const fileData = JSON.parse(e.dataTransfer.getData('text/plain'));

                // Add file to destination structure
                const pathParts = destPath.split('/').filter(part => part !== '');
                let currentNode = destinationStructure;

                // Navigate to the target folder
                for (let i = 1; i < pathParts.length; i++) {
                    const part = pathParts[i];
                    currentNode = currentNode.children.find(child => child.name === part);
                    if (!currentNode) break;
                }

                if (currentNode && currentNode.type === 'folder') {
                    // Add the file to the destination folder
                    const newFile = {
                        name: fileData.name,
                        type: 'file',
                        uploadTime: new Date().toLocaleTimeString(),
                        sourcePath: fileData.path
                    };
                    currentNode.children.push(newFile);

                    // Track the upload
                    const fileInfo = {
                        name: fileData.name,
                        sourcePath: fileData.path,
                        destPath: destPath,
                        uploadTime: newFile.uploadTime,
                        status: 'Uploaded'
                    };

                    droppedFiles.push(fileInfo);

                    // Update the destination tree
                    usercacheFileManager.updateDestinationTree();

                    // Show success notification
                    showSuccessNotification(`${fileData.name} attached to ${destPath}`);
                }
            }

            // Folder renaming functionality
            usercacheFileManager.startEdit = function(folderPath, nodeId) {
                const nameElement = document.getElementById(`name-${nodeId}`);
                const currentName = nameElement.textContent;

                // Create input element
                const input = document.createElement('input');
                input.type = 'text';
                input.value = currentName;
                input.className = 'folder-name editing';
                input.style.width = '100%';

                // Replace the span with input
                nameElement.style.display = 'none';
                nameElement.parentNode.insertBefore(input, nameElement.nextSibling);

                // Focus and select the text
                input.focus();
                input.select();

                // Handle save on Enter or blur
                const saveEdit = () => {
                    const newName = input.value.trim();
                    if (newName && newName !== currentName) {
                        renameFolder(folderPath, newName);
                    }
                    input.remove();
                    nameElement.style.display = 'inline';
                };

                // Handle cancel on Escape
                const cancelEdit = () => {
                    input.remove();
                    nameElement.style.display = 'inline';
                };

                input.addEventListener('blur', saveEdit);
                input.addEventListener('keydown', (e) => {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        saveEdit();
                    } else if (e.key === 'Escape') {
                        e.preventDefault();
                        cancelEdit();
                    }
                });
            }

            function renameFolder(oldPath, newName) {
                // Find the folder in the destination structure
                const pathParts = oldPath.split('/').filter(part => part !== '');
                let currentNode = destinationStructure;
                let parentNode = null;
                let targetNode = null;

                // Navigate to the target folder
                for (let i = 1; i < pathParts.length; i++) {
                    const part = pathParts[i];
                    parentNode = currentNode;
                    targetNode = currentNode.children.find(child => child.name === part);
                    currentNode = targetNode;
                    if (!currentNode) break;
                }

                if (targetNode) {
                    // Update the folder name
                    targetNode.name = newName;

                    // Update expanded folders set
                    expandedDestFolders.delete(oldPath);
                    const newPath = pathParts.slice(0, -1).join('/') + '/' + newName;
                    if (pathParts.length > 1) {
                        expandedDestFolders.add(newPath);
                    } else {
                        expandedDestFolders.add(newName);
                    }

                    // Update the tree
                    usercacheFileManagerupdateDestinationTree();

                    // Show success notification
                    showSuccessNotification(`Folder renamed to "${newName}"`);
                }
            }

            function showContextMenu(event, folderPath) {
                event.preventDefault();
                event.stopPropagation();

                // Remove existing context menu
                const existingMenu = document.querySelector('.context-menu');
                if (existingMenu) {
                    existingMenu.remove();
                }

                // Create context menu
                const menu = document.createElement('div');
                menu.className = 'context-menu';
                menu.style.left = event.pageX + 'px';
                menu.style.top = event.pageY + 'px';

                menu.innerHTML = `
                    <div class="context-menu-item" onclick="startEditFromContext('${folderPath}')">
                        <span>✏️</span>
                        <span>Rename</span>
                    </div>
                    <div class="context-menu-item" onclick="addNewFolder('${folderPath}')">
                        <span>📁</span>
                        <span>New Folder</span>
                    </div>
                `;

                document.body.appendChild(menu);

                // Close menu on click outside
                const closeMenu = (e) => {
                    if (!menu.contains(e.target)) {
                        menu.remove();
                        document.removeEventListener('click', closeMenu);
                    }
                };

                setTimeout(() => {
                    document.addEventListener('click', closeMenu);
                }, 100);
            }

            function startEditFromContext(folderPath) {
                const nodeId = `dest-${folderPath.replace(/[^a-zA-Z0-9]/g, '-')}`;
                startEdit(folderPath, nodeId);

                // Remove context menu
                const menu = document.querySelector('.context-menu');
                if (menu) menu.remove();
            }

            function addNewFolder(parentPath) {
                // Find the parent folder
                const pathParts = parentPath.split('/').filter(part => part !== '');
                let currentNode = destinationStructure;

                for (let i = 1; i < pathParts.length; i++) {
                    const part = pathParts[i];
                    currentNode = currentNode.children.find(child => child.name === part);
                    if (!currentNode) break;
                }

                if (currentNode && currentNode.type === 'folder') {
                    // Add new folder
                    const newFolderName = `New Folder ${currentNode.children.filter(c => c.type === 'folder').length + 1}`;
                    const newFolder = {
                        name: newFolderName,
                        type: 'folder',
                        children: []
                    };

                    currentNode.children.push(newFolder);

                    // Expand parent folder
                    expandedDestFolders.add(parentPath);

                    // Update tree
                    updateDestinationTree();

                    // Start editing the new folder name
                    setTimeout(() => {
                        const newPath = parentPath + '/' + newFolderName;
                        const nodeId = `dest-${newPath.replace(/[^a-zA-Z0-9]/g, '-')}`;
                        startEdit(newPath, nodeId);
                    }, 100);
                }

                const logContainer = document.getElementById('uploadLog');
                logContainer.innerHTML = droppedFiles.map(file => `
                    <div class="upload-log-item">
                        <span class="icon">${getFileIcon(file.name)}</span>
                        <div style="flex: 1;">
                            <div style="font-weight: 600;">${file.name}</div>
                            <div class="path-display">From: ${file.sourcePath}</div>
                            <div class="path-display">To: ${file.destPath}</div>
                        </div>
                        <div style="text-align: right;">
                            <div class="status-indicator">${file.status}</div>
                            <div style="font-size: 12px; color: #666; margin-top: 2px;">${file.uploadTime}</div>
                        </div>
                    </div>
                `).join('');
            }

            function showSuccessNotification(message) {
                const notification = document.createElement('div');
                notification.style.cssText = `
                    position: fixed;
                    top: 20px;
                    right: 20px;
                    background: rgba(40, 167, 69, 0.9);
                    color: white;
                    padding: 15px 20px;
                    border-radius: 8px;
                    box-shadow: 0 4px 12px rgba(0,0,0,0.3);
                    z-index: 1000;
                    font-weight: 600;
                    animation: slideIn 0.3s ease-out;
                `;
                notification.textContent = message;
                document.body.appendChild(notification);

                setTimeout(() => {
                    notification.remove();
                }, 3000);
            }

         usercacheFileManager.init = function() {
            // Initialize
            usercacheFileManager.updateSourceTree();
            usercacheFileManager.updateDestinationTree();

            // Close context menu on outside click
            document.addEventListener('click', (e) => {
                if (!e.target.closest('.context-menu')) {
                    const menu = document.querySelector('.context-menu');
                    if (menu) menu.remove();
                }
            });
         }

        usercacheFileManager.init();

        return XNAT.app.usercacheFileManager = usercacheFileManager;


}))