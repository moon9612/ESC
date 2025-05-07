window.onload = function () {
    // ✅ 카드 슬라이드 초기화 (1개씩 넘기기)
    let currentIndexTop = 0;
    const cardContainerTop = document.querySelector('.card-container');
    const cardsTop = document.querySelectorAll('.card');
    const cardCount = cardsTop.length;

    // 카드 너비 + gap (237px + 32px)
    const cardMoveAmount = 269;

    // 한 화면에 보이는 카드 수 계산 (선택사항, 페이지 정보에 사용)
    const cardsPerView = 3;

    const updateSlideTop = () => {
        const offset = currentIndexTop * cardMoveAmount;
        cardContainerTop.style.transform = `translateX(-${offset}px)`;

        const pageInfo = document.getElementById('cardPageInfoTop');
        const totalPages = cardCount - cardsPerView + 1; // 페이지 수 = 보여줄 수 있는 최대 슬라이드 횟수
        pageInfo.textContent = `${currentIndexTop + 1} / ${totalPages}`;
    };

    document.getElementById('cardPrevBtnTop').addEventListener('click', () => {
        if (currentIndexTop > 0) {
            currentIndexTop--;
            updateSlideTop();
        }
    });

    document.getElementById('cardNextBtnTop').addEventListener('click', () => {
        if (currentIndexTop < cardCount - cardsPerView) {
            currentIndexTop++;
            updateSlideTop();
        }
    });

    updateSlideTop();


    // ✅ 스크롤 애니메이션
    const animateOnScroll = () => {
        const sections = document.querySelectorAll(".full-screen-section, .hero");

        sections.forEach((section) => {
            const sectionTop = section.getBoundingClientRect().top;
            const windowHeight = window.innerHeight;
            const isVisible = sectionTop < windowHeight * 0.7;

            if (section.classList.contains("full-screen-section")) {
                const content = section.querySelector("div");
                content.style.opacity = isVisible ? "1" : "0";
                content.style.transform = isVisible ? "translateY(0)" : "translateY(30px)";
            } else if (section.classList.contains("hero")) {
                const textBox = section.querySelector(".text-box");
                const imageBox = section.querySelector(".image-box");
                textBox.style.opacity = isVisible ? "1" : "0";
                imageBox.style.opacity = isVisible ? "1" : "0";
                textBox.style.transform = isVisible ? "translateX(0)" : "translateX(-30px)";
                imageBox.style.transform = isVisible ? "translateX(0)" : "translateX(30px)";
            }
        });
    };

    window.addEventListener("scroll", animateOnScroll);
    animateOnScroll();

    // ✅ 스크롤 다운 버튼
    document.querySelector(".scroll-down").addEventListener("click", () => {
        const hero = document.querySelector(".hero");
        window.scrollTo({
            top: hero.offsetTop,
            behavior: "smooth",
        });
    });

    // ✅ 헤더 스크롤 반응
    window.addEventListener('scroll', () => {
        const header = document.querySelector('.main-header');
        if (window.scrollY > 50) {
            header.classList.add('scrolled');
        } else {
            header.classList.remove('scrolled');
        }
    });

    // ✅ 모바일 메뉴 토글
    document.querySelector('.menu-toggle').addEventListener('click', () => {
        document.querySelector('.nav-menu').classList.toggle('active');
    });
};
