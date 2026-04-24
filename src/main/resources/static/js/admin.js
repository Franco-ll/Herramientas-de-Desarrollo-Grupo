document.addEventListener("DOMContentLoaded", function () {
    console.log("Panel de administración cargado correctamente");

    const menuLinks = document.querySelectorAll(".menu a");

    menuLinks.forEach(link => {
        link.addEventListener("click", function () {
            menuLinks.forEach(item => item.classList.remove("active"));
            this.classList.add("active");
        });
    });
});