function highlight(){
  var menuItems = document.querySelectorAll('#nav li a');
  var url = document.location.href;
  for (var i = 0; i < menuItems.length; i++) {
      if (url === menuItems[i].href) {
          menuItems[i].className = "selected";
      } else {
          menuItems[i].className = "";
      }
  }  
}

highlight();
