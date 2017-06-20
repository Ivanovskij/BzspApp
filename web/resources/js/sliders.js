var sl__slide = document.getElementById('sl__slide');


/**** mouse over, out *****/
sl__slide.onmouseover = function (e) {
    addStopClass();
}
sl__slide.onmouseout = function (e) {
    addRunClass();
}

function addStopClass() {
    sl__slide.classList.add("stop__slider");
}
function addRunClass() {
    sl__slide.classList.remove("stop__slider");
}
function addImg1Class() {
    sl__slide.classList.add("sl__slide_img1");
}
function addImg2Class() {
    sl__slide.classList.add("sl__slide_img2");
}
function addImg3Class() {
    sl__slide.classList.add("sl__slide_img3");
}
function addImg4Class() {
    sl__slide.classList.add("sl__slide_img4");
}

function removeAllClassImg() {
    sl__slide.classList.remove("sl__slide_img1");
    sl__slide.classList.remove("sl__slide_img2");
    sl__slide.classList.remove("sl__slide_img3");
    sl__slide.classList.remove("sl__slide_img4");
}



/****** checked radio *******/
var radio1 = document.getElementById('slide1');
var radio2 = document.getElementById('slide2');
var radio3 = document.getElementById('slide3');
var radio4 = document.getElementById('slide4');

radio1.onclick = function () {
    // когда пользователь переключает постоянно
    // radiobutton нужно очищать таймер
    // который хочет запустить анимацию вновь
    clearTimeout(timeout);
    removeAllClassImg();
    sl__slide.style.animation = 'none';
    addImg1Class();
    timeout = setTimeout(runAnimation, 5000);
}
radio2.onclick = function () {
    clearTimeout(timeout);
    removeAllClassImg();
    sl__slide.style.animation = 'none';
    addImg2Class();
    timeout = setTimeout(runAnimation, 5000);
}
radio3.onclick = function () {
    clearTimeout(timeout);
    removeAllClassImg();
    sl__slide.style.animation = 'none';
    addImg3Class();
    timeout = setTimeout(runAnimation, 5000);
}
radio4.onclick = function () {
    clearTimeout(timeout);
    removeAllClassImg();
    sl__slide.style.animation = 'none';
    addImg4Class();
    timeout = setTimeout(runAnimation, 5000);
}
// запускаем слайдер вновь
let timeout;

var runAnimation = function () {
    removeAllClassImg();
    sl__slide.style.animation = 'img_spin 10s ease-in-out infinite';
}