var fileInputTextDiv = document.getElementById('file_input_text_div');
var fileInput = document.getElementById('file_input_file');
var fileInputText = document.getElementById('file_input_text');

fileInput.addEventListener('change', changeInputText);
fileInput.addEventListener('change', changeState);

function changeInputText() {
    var str = fileInput.value;
    var i;
    if (str.lastIndexOf('\\')) {
        i = str.lastIndexOf('\\') + 1;
    } else if (str.lastIndexOf('/')) {
        i = str.lastIndexOf('/') + 1;
    }
    fileInputText.value = str.slice(i, str.length);
}

function changeState() {
    if (fileInputText.value.length != 0) {
        if (!fileInputTextDiv.classList.contains("is-focused")) {
            fileInputTextDiv.classList.add('is-focused');
        }
    } else {
        if (fileInputTextDiv.classList.contains("is-focused")) {
            fileInputTextDiv.classList.remove('is-focused');
        }
    }
}

$(document).ready(function() {

    $('#convert-file-form').submit(function(event) {

        if($('input[name=file]')[0].files.length == 0){
            swal({
                title: 'Selecione um arquivo para a conversão!',
                type: 'error',
                confirmButtonText: 'Ok',
                allowOutsideClick: false,
            });
        }else {

            swal({
                title: 'Realizando o upload do arquivo...',
                allowOutsideClick: false,
                onOpen: () => {
                    swal.showLoading()
                }
            });

            var formData = {
                'file': $('input[name=file]').val()
            };

            var data = new FormData();
            data.append('file', $('#file_input_file')[0].files[0]);

            $.ajax({
                url: '/convert',
                data: data,
                cache: false,
                contentType: false,
                processData: false,
                method: 'POST',
                type: 'POST',
                enctype: 'multipart/form-data',
                success: function (data) {
                    swal.close();
                    swal({
                        title: 'Convertendo o arquivo...',
                        allowOutsideClick: false,
                        onOpen: () => {
                            swal.showLoading()
                        }
                    });

                    var response = JSON.parse(data);

                    if(response.status == "error"){
                        swal.close();
                        swal({
                            title: 'Aconteceu um erro!',
                            text: "Você tem certeza que tentou converter um arquivo de vídeo?",
                            type: 'error',
                            confirmButtonText: 'Tentar novamente',
                            allowOutsideClick: false,
                            onOpen: () => {
                                swal.hideLoading()
                            },
                            onClose: () => {
                                document.getElementById("convert-file-form").reset();
                            }
                        });
                    }else{
                        var apiResposeData = JSON.parse(response.message);
                        var fileURL = response.fileUrl;
                        var jobId =apiResposeData.id;

                        checkConvertingProgress(jobId, fileURL);
                    }
                },
                error: function (XMLHttpRequest) {
                    swal({
                        title: 'Aconteceu um erro inesperado!',
                        type: 'error',
                        confirmButtonText: 'Tentar novamente',
                        allowOutsideClick: false,
                        onOpen: () => {
                            swal.hideLoading()
                        },
                        onClose: () => {
                            document.getElementById("convert-file-form").reset();
                        }
                    });
                }

            });
        }

        event.preventDefault();
    });

    function checkConvertingProgress(jobId, fileUrl){

        var data = new FormData();
        data.append('jobId', jobId);

        $.ajax({
            url: '/progress',
            data: data,
            cache: false,
            contentType: false,
            processData: false,
            method: 'POST',
            type: 'POST',
            enctype: 'multipart/form-data',
            success: function (data) {

                var response = JSON.parse(data);

                if(response.status == "success") {

                    var zencoderResponse = JSON.parse(response.message);

                    if (zencoderResponse.state == "failed" || zencoderResponse.state == "cancelled") {

                        document.getElementById("convert-file-form").reset();

                        swal({
                            title: 'Aconteceu um erro!',
                            text: "Você tem certeza que tentou converter um arquivo de vídeo?",
                            type: 'error',
                            confirmButtonText: 'Tentar novamente',
                            allowOutsideClick: false,
                            onOpen: () => {
                                swal.hideLoading()
                            },
                            onClose: () => {
                                document.getElementById("convert-file-form").reset();
                            }
                        });
                    } else if (zencoderResponse.state == "finished") {

                        swal({
                            title: 'Sua conversão terminou!',
                            type: 'success',
                            confirmButtonText: 'Assistir vídeo',
                            allowOutsideClick: false,
                        }).then(() => {
                            $('#upload-container').hide();
                            $('#video-container video').attr('src', fileUrl);
                            $('#video-container').show();
                        });
                    } else {
                        checkConvertingProgress(jobId, fileUrl);
                    }
                } else{
                    swal({
                        title: 'Aconteceu um erro!',
                        text: "Você tem certeza que tentou converter um arquivo de vídeo?",
                        type: 'error',
                        confirmButtonText: 'Tentar novamente',
                        allowOutsideClick: false,
                        onOpen: () => {
                            swal.hideLoading()
                        },
                        onClose: () => {
                            document.getElementById("convert-file-form").reset();
                        }
                    });
                }

            },
            error: function (XMLHttpRequest) {
                swal({
                    title: 'Aconteceu um erro inesperado!',
                    type: 'error',
                    confirmButtonText: 'Tentar novamente',
                    allowOutsideClick: false,
                    onOpen: () => {
                        swal.hideLoading()
                    },
                    onClose: () => {
                        document.getElementById("convert-file-form").reset();
                    }
                });
            }

        });
    }
});