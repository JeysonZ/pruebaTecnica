document.addEventListener('DOMContentLoaded', function() {
    const fileInput = document.getElementById('fileInput');
    const uploadExcelButton = document.getElementById('uploadExcel');
    const uploadPdfButton = document.getElementById('uploadPdf');
    const message = document.getElementById('message');
    const loadDocumentsButton = document.getElementById('loadDocuments');
    const fileList = document.getElementById('fileList').getElementsByTagName('tbody')[0];

    // Los botones deshabilitados
    setButtonState(false, false);

    fileInput.addEventListener('change', function() {
        const file = fileInput.files[0];
        if (file) {
            const fileType = file.type;
            if (fileType === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') {
                setButtonState(true, false);
            } else if (fileType === 'application/pdf') {
                setButtonState(false, true);
            } else {
                setButtonState(false, false);
            }
        } else {
            setButtonState(false, false);
        }
    });

    uploadExcelButton.addEventListener('click', function() {
        uploadFile('excel');
    });

    uploadPdfButton.addEventListener('click', function() {
        uploadFile('pdf');
    });

    loadDocumentsButton.addEventListener('click', function() {
        fetchDocuments();
    });

    function uploadFile(type) {
        const file = fileInput.files[0];
        if (file) {
            const formData = new FormData();
            formData.append('file', file);

            fetch(`/upload/${type}`, {
                method: 'POST',
                body: formData
            })
            .then(response => {
                if (response.ok) {
                    message.style.display = 'block';
                    fileInput.value = '';
                    setButtonState(false, false);
                    fetchDocuments();
                } else {
                    alert('Error al cargar el archivo.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
        }
    }

    function setButtonState(excelEnabled, pdfEnabled) {
        uploadExcelButton.disabled = !excelEnabled;
        uploadPdfButton.disabled = !pdfEnabled;

        if (excelEnabled) {
            uploadExcelButton.classList.add('green');
            uploadExcelButton.classList.remove('disabled');
        } else {
            uploadExcelButton.classList.remove('green');
            uploadExcelButton.classList.add('disabled');
        }

        if (pdfEnabled) {
            uploadPdfButton.classList.add('red');
            uploadPdfButton.classList.remove('disabled');
        } else {
            uploadPdfButton.classList.remove('red');
            uploadPdfButton.classList.add('disabled');
        }
    }

    function fetchDocuments() {
        fetch('/documents')
            .then(response => response.json())
            .then(data => {
                fileList.innerHTML = '';
                data.forEach(doc => {
                    const row = fileList.insertRow();
                    const cellName = row.insertCell(0);
                    const cellType = row.insertCell(1);
                    const cellSize = row.insertCell(2);
                    const cellDate = row.insertCell(3);

                    cellName.textContent = doc.fileName;
                    cellType.textContent = doc.fileType;
                    cellSize.textContent = doc.fileSize;
                    cellDate.textContent = new Date(doc.uploadDate).toLocaleString();
                });
            })
            .catch(error => console.error('Error:', error));
    }

    window.closeMessage = function() {
        message.style.display = 'none';
    };
});
