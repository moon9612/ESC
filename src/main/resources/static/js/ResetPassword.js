window.onload = function () {
    const passwordInput = document.querySelector("input[name='pw']");
    const lengthCheck = document.getElementById("length-check");
    const uppercaseCheck = document.getElementById("uppercase-check");
    const numberCheck = document.getElementById("number-check");
    const specialCheck = document.getElementById("special-check");

    // 비밀번호 조건을 체크하는 함수
    function validatePassword(password) {
        const checks = {
            length: password.length >= 8,
            uppercase: /[a-z]/.test(password) && /[A-Z]/.test(password),
            number: /\d/.test(password),
            special: /[!@#$%^&*(),.?":{}|<>]/.test(password)
        };

        // 체크 상태를 업데이트
        updateCheck(lengthCheck, checks.length);
        updateCheck(uppercaseCheck, checks.uppercase);
        updateCheck(numberCheck, checks.number);
        updateCheck(specialCheck, checks.special);
    }

    // 체크 상태를 업데이트하는 함수
    function updateCheck(element, passed) {
        const icon = element.querySelector("i");
        if (passed) {
            icon.className = "ri-checkbox-circle-line";  // 체크 아이콘
            element.style.color = "green";  // 체크되면 초록색
        } else {
            icon.className = "ri-checkbox-blank-circle-line";  // 빈 체크박스 아이콘
            element.style.color = "gray";  // 충족되지 않으면 회색
        }
    }

    // 비밀번호 입력 시 유효성 검사
    passwordInput.addEventListener("input", function () {
        validatePassword(passwordInput.value);
    });
}
