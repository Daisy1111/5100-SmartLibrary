$(document).ready(function() {

    $(".delete-book").click(function() {
        var result = confirm($(this).data('confirmation-message'));
        if(!result)
            return false;

        var url = $(this).data('delete-url');
        var redirectUrl = $(this).data('redirect-url');

        $.ajax({
            url: url,
            type: 'delete'
        }).done(function() {
            location.href=redirectUrl;
        });
    });



    initTooltips();

    initPopovers();
});

function initTooltips() {
    $('.display-tooltip').tooltip();
}

function initPopovers() {
    $('.popover-link').each(function() {
        var name = $(this).data('name');
        var url = $(this).data('url');

        $(this).popover({
            html: true,
            content: function() {
                var result;
                $.ajax({
                    url: url,
                    type: 'get',
                    async: false
                }).done(function(data) {
                    result = data;

                });
                return result;
            },
            title: name,
            animation: true,
            trigger: 'click'
        });
    });
}