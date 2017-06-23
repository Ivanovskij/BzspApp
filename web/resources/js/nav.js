$(function () {
    var url = window.location.pathname;
    var activePage = url.substring(url.lastIndexOf('/') + 1);
    $('#form-nav li a').each(function () {
        var linkPage = this.href.substring(this.href.lastIndexOf('/') + 1);

        if (activePage === linkPage) {
            $(this).parent().addClass('active');
        }
    });
});