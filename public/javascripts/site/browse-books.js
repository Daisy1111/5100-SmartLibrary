$(document).ready(function() {
    /* SOME UNINPORTANT EFFECTS */
    $(".book-item").mouseenter(function() {
        $(this).css("border-color", "#B5B5B5");
    });

    $(".book-item").mouseleave(function() {
        $(this).css("border-color", "#E6E6E6");
    });

    /* HIGHLIGHT THE SEARCHED PHRASE ON FIRST LOAD (NON-AJAX) */
    highlightBookTitles();

    /* UPDATE BOOK LIST WHEN TYPING TO THE SEARCH FIELD */
    $('.search-query').keyup(function() {
        var text = $(this).val();
        refreshBooks();
    });

    /* SEARCH ACCORDING TO AUTHORS */
    $('.browse-books .author-list a').click(function() {
        var hasAlreadyClass = $(this).hasClass('selected');

        $('.browse-books .author-list a').each(function() {
            $(this).removeClass('selected');
        });

        if(!hasAlreadyClass) {
            $(this).addClass('selected');
        }

        refreshBooks();
    });

    $('.browse-books .tag-list .tag').click(function() {
        var hasAlreadyClass = $(this).hasClass('label-important');

        $('.browse-books .tag-list .tag').each(function() {
            $(this).removeClass('label-important');
            $(this).addClass('label-info');
        });

        if(!hasAlreadyClass) {
            $(this).removeClass('label-info');
            $(this).addClass('label-important');
        }

        refreshBooks();
    });
});



function refreshBooks() {
    var selectedAuthor = $('.browse-books .author-list a.selected');
    var selectedTag = $('.browse-books .tag-list .tag.label-important');

    var data = {}
    data.q = $('.search-query').val();
    if(selectedAuthor.length > 0) {
        data.authorId = selectedAuthor.data('author-id')
    }
    if(selectedTag.length > 0) {
        data.tagId = selectedTag.data('tag-id');
    }

    $.ajax({
        url: $('.browse-books .book-list').data('update-url'),
        type: 'get',
        data: data
    }).done(function(data) {
       $('.browse-books .book-list').html(data);
       initPopovers();
       highlightBookTitles();
    });
};

function highlightBookTitles() {
    $('.book-item .title a').each(function() {
        var query = $('.search-query').val();
        var regex = new RegExp( '(' + query + ')', 'gi' );
        var text = $(this).text().replace(regex, "<span class='search-highlight'>$1</span>");
        $(this).html(text);

    });

};