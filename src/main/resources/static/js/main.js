window.onload = function () {

    const animateOnScroll = () => {
        const sections = document.querySelectorAll(".full-screen-section, .hero");

        sections.forEach((section) => {
            const sectionTop = section.getBoundingClientRect().top;
            const windowHeight = window.innerHeight;

            if (sectionTop < windowHeight * 0.7) {
                if (section.classList.contains("full-screen-section")) {
                    section.querySelector("div").style.opacity = "1";
                    section.querySelector("div").style.transform = "translateY(0)";
                } else if (section.classList.contains("hero")) {
                    section.querySelector(".text-box").style.opacity = "1";
                    section.querySelector(".text-box").style.transform = "translateX(0)";
                    section.querySelector(".image-box").style.opacity = "1";
                    section.querySelector(".image-box").style.transform = "translateX(0)";
                }
            } else {
                if (section.classList.contains("full-screen-section")) {
                    section.querySelector("div").style.opacity = "0";
                    section.querySelector("div").style.transform = "translateY(30px)";
                } else if (section.classList.contains("hero")) {
                    section.querySelector(".text-box").style.opacity = "0";
                    section.querySelector(".text-box").style.transform =
                        "translateX(-30px)";
                    section.querySelector(".image-box").style.opacity = "0";
                    section.querySelector(".image-box").style.transform =
                        "translateX(30px)";
                }
            }
        });
    };
    window.addEventListener("scroll", animateOnScroll);
    animateOnScroll(); // Initial check on load

    window.scrollTo({
        top: document.querySelector(".hero").offsetTop,
        behavior: "smooth",
    });

}