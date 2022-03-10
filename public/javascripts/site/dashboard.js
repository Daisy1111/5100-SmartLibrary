$(document).ready(function() {
    $('.db-cleanup').click(function() {
        var url = $(this).data('cleanup-url');
        var question = $(this).data('cleanup-question');
        var message = $(this).data('cleanup-message');

        $.ajax({
            url: url,
            type: 'post',
            beforeSend: function(xhr) {
                var result = confirm(question);
                if(!result) xhr.abort();
            }
        }).done(function(data) {
            alert(message.replace('{0}', data));
        });
    });
})