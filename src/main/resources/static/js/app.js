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

function progress(e){

    if(e.lengthComputable){
        var max = e.total;
        var current = e.loaded;

        var percentage = (current * 100)/max;

        var value = percentage + '%';
        $("#bar").animate({width: value}, 75, 'linear', function() {
            $('#progress-bar-label')[0].innerHTML = Math.round(percentage) + "%";
        });
    }
}

$(document).ready(function() {

    $('#convert-file-form').submit(function(event) {

        $('#progress-bar-container').show();

        var formData = {
            'file' : $('input[name=file]').val()
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
            enctype     : 'multipart/form-data',
            xhr: function() {
                var myXhr = $.ajaxSettings.xhr();
                if(myXhr.upload){
                    myXhr.upload.addEventListener('progress',progress, false);
                }
                return myXhr;
            },
            success: function(url){
                $('#upload-container').hide();
                $('#progress-bar-container').hide();
                $('#video-container video').attr('src',url)
                $('#video-container').show();
            },
            error: function(XMLHttpRequest) {
                alert('Error');
            }

        });

        event.preventDefault();
    });

});