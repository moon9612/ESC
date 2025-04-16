window.onload = function () {
  const loginContainer = document.getElementById("loginContainer");
  const signupContainer = document.getElementById("signupContainer");
  const forgotPasswordContainer = document.getElementById(
    "forgotPasswordContainer",
  );
  const signupLink = document.getElementById("signupLink");
  const forgotPasswordLink = document.getElementById("forgotPasswordLink");
  const backToLogin = document.getElementById("backToLogin");
  const backToLoginFromForgot = document.getElementById(
    "backToLoginFromForgot",
  );
  // 비밀번호 찾기 페이지 전환
  forgotPasswordLink.addEventListener("click", function (e) {
    e.preventDefault();
    loginContainer.style.display = "none";
    forgotPasswordContainer.style.display = "block";
    window.scrollTo(0, 0);
  });
  backToLoginFromForgot.addEventListener("click", function (e) {
    e.preventDefault();
    forgotPasswordContainer.style.display = "none";
    loginContainer.style.display = "block";
    window.scrollTo(0, 0);
  });
  // 비밀번호 찾기 폼 검증
  const forgotPasswordForm = document.getElementById("forgotPasswordForm");
  const forgotEmail = document.getElementById("forgotEmail");
  const forgotEmailError = document.getElementById("forgotEmailError");
  const forgotEmailSuccess = document.getElementById("forgotEmailSuccess");
  forgotPasswordForm.addEventListener("submit", function (e) {
    e.preventDefault();
    if (!forgotEmail.value.trim()) {
      forgotEmailError.textContent = "이메일 주소를 입력해주세요.";
      forgotEmailError.classList.add("show");
      forgotEmailSuccess.classList.remove("show");
    } else if (!isValidEmail(forgotEmail.value)) {
      forgotEmailError.textContent = "유효한 이메일 주소를 입력해주세요.";
      forgotEmailError.classList.add("show");
      forgotEmailSuccess.classList.remove("show");
    } else {
      forgotEmailError.classList.remove("show");
      forgotEmailSuccess.classList.add("show");
      // 여기에 비밀번호 재설정 이메일 전송 로직 추가
      setTimeout(() => {
        forgotPasswordForm.reset();
        forgotEmailSuccess.classList.remove("show");
        forgotPasswordContainer.style.display = "none";
        loginContainer.style.display = "block";
      }, 3000);
    }
  });
  signupLink.addEventListener("click", function (e) {
    e.preventDefault();
    loginContainer.style.display = "none";
    signupContainer.style.display = "block";
    window.scrollTo(0, 0);
  });
  backToLogin.addEventListener("click", function (e) {
    e.preventDefault();
    signupContainer.style.display = "none";
    loginContainer.style.display = "block";
    window.scrollTo(0, 0);
  });
  // 버튼 리플 효과
  const buttons = document.querySelectorAll(".btn");
  buttons.forEach((button) => {
    button.addEventListener("click", function (e) {
      const x = e.clientX - e.target.getBoundingClientRect().left;
      const y = e.clientY - e.target.getBoundingClientRect().top;
      const ripple = document.createElement("span");
      ripple.classList.add("btn-ripple");
      ripple.style.left = `${x}px`;
      ripple.style.top = `${y}px`;
      this.appendChild(ripple);
      setTimeout(() => {
        ripple.remove();
      }, 600);
    });
  });
  // 로그인 폼 검증
  const loginForm = document.getElementById("loginForm");
  const emailInput = document.getElementById("email");
  const passwordInput = document.getElementById("password");
  const emailError = document.getElementById("emailError");
  const passwordError = document.getElementById("passwordError");
  loginForm.addEventListener("submit", function (e) {
    e.preventDefault();
    let isValid = true;
    // 이메일 검증
    if (!emailInput.value.trim()) {
      emailError.textContent = "이메일 주소를 입력해주세요.";
      emailError.classList.add("show");
      isValid = false;
    } else if (!isValidEmail(emailInput.value)) {
      emailError.textContent = "유효한 이메일 주소를 입력해주세요.";
      emailError.classList.add("show");
      isValid = false;
    } else {
      emailError.classList.remove("show");
    }
    // 비밀번호 검증
    if (!passwordInput.value.trim()) {
      passwordError.textContent = "비밀번호를 입력해주세요.";
      passwordError.classList.add("show");
      isValid = false;
    } else {
      passwordError.classList.remove("show");
    }
    if (isValid) {
      // 여기에 로그인 로직 추가
      alert("로그인 성공!");
      loginForm.reset();
    }
  });
  // 회원가입 비밀번호 강도 체크
  const signupPassword = document.getElementById("signupPassword");
  const strengthMeter = document.getElementById("strengthMeter");
  const strengthText = document.getElementById("strengthText");
  signupPassword.addEventListener("input", function () {
    const password = this.value;
    let strength = 0;
    if (password.length >= 8) strength += 1;
    if (password.match(/[a-z]+/)) strength += 1;
    if (password.match(/[A-Z]+/)) strength += 1;
    if (password.match(/[0-9]+/)) strength += 1;
    if (password.match(/[^a-zA-Z0-9]+/)) strength += 1;
    switch (strength) {
      case 0:
        strengthMeter.className = "strength-meter";
        strengthMeter.style.width = "0";
        strengthText.textContent = "8자 이상, 영문, 숫자, 특수문자 포함";
        break;
      case 1:
      case 2:
        strengthMeter.className = "strength-meter weak";
        strengthText.textContent = "약함";
        break;
      case 3:
      case 4:
        strengthMeter.className = "strength-meter medium";
        strengthText.textContent = "보통";
        break;
      case 5:
        strengthMeter.className = "strength-meter strong";
        strengthText.textContent = "강함";
        break;
    }
    validateSignupForm();
  });
  // 비밀번호 확인 검증
  const confirmPassword = document.getElementById("confirmPassword");
  const confirmPasswordError = document.getElementById("confirmPasswordError");
  confirmPassword.addEventListener("input", function () {
    if (this.value !== signupPassword.value) {
      confirmPasswordError.textContent = "비밀번호가 일치하지 않습니다.";
      confirmPasswordError.classList.add("show");
    } else {
      confirmPasswordError.classList.remove("show");
    }
    validateSignupForm();
  });
  // 이메일 검증
  const signupEmail = document.getElementById("signupEmail");
  const signupEmailError = document.getElementById("signupEmailError");
  const signupEmailSuccess = document.getElementById("signupEmailSuccess");
  signupEmail.addEventListener("blur", function () {
    if (!this.value.trim()) {
      signupEmailError.textContent = "이메일 주소를 입력해주세요.";
      signupEmailError.classList.add("show");
      signupEmailSuccess.classList.remove("show");
    } else if (!isValidEmail(this.value)) {
      signupEmailError.textContent = "유효한 이메일 주소를 입력해주세요.";
      signupEmailError.classList.add("show");
      signupEmailSuccess.classList.remove("show");
    } else {
      // 여기에서 이메일 중복 체크 로직을 추가할 수 있습니다.
      signupEmailError.classList.remove("show");
      signupEmailSuccess.classList.add("show");
    }
    validateSignupForm();
  });
  // 이름 검증
  const nameInput = document.getElementById("name");
  const nameError = document.getElementById("nameError");
  nameInput.addEventListener("blur", function () {
    if (!this.value.trim()) {
      nameError.textContent = "이름을 입력해주세요.";
      nameError.classList.add("show");
    } else {
      nameError.classList.remove("show");
    }
    validateSignupForm();
  });
  // 생년월일 검증
  const birthDateInput = document.getElementById("birthDate");
  const birthDateError = document.getElementById("birthDateError");
  birthDateInput.addEventListener("blur", function () {
    if (!this.value) {
      birthDateError.textContent = "생년월일을 입력해주세요.";
      birthDateError.classList.add("show");
    } else {
      const selectedDate = new Date(this.value);
      const today = new Date();
      const minDate = new Date(
        today.getFullYear() - 100,
        today.getMonth(),
        today.getDate(),
      );
      if (selectedDate > today) {
        birthDateError.textContent = "미래의 날짜는 선택할 수 없습니다.";
        birthDateError.classList.add("show");
      } else if (selectedDate < minDate) {
        birthDateError.textContent = "유효하지 않은 생년월일입니다.";
        birthDateError.classList.add("show");
      } else {
        birthDateError.classList.remove("show");
      }
    }
    validateSignupForm();
  });

  // 회원가입 폼 검증
  const signupForm = document.getElementById("signupForm");
  const signupBtn = document.getElementById("signupBtn");
  function validateSignupForm() {
    const isEmailValid =
      signupEmail.value.trim() && isValidEmail(signupEmail.value);
    const isPasswordValid =
      signupPassword.value.trim() && signupPassword.value.length >= 8;
    const isConfirmPasswordValid =
      confirmPassword.value === signupPassword.value;
    const isNameValid = nameInput.value.trim();
    const isBirthDateValid =
      birthDateInput.value && !birthDateError.classList.contains("show");
    if (
      isEmailValid &&
      isPasswordValid &&
      isConfirmPasswordValid &&
      isNameValid &&
      isBirthDateValid
    ) {
      signupBtn.classList.add("active");
    } else {
      signupBtn.classList.remove("active");
    }
  }
  // signupForm.addEventListener("submit", function (e) {
  //   e.preventDefault();
  //   if (signupBtn.classList.contains("active")) {
  //     // 여기에 회원가입 로직 추가
  //     alert("회원가입 성공!");
  //     signupContainer.style.display = "none";
  //     loginContainer.style.display = "block";
  //   }
  // });
  // 유틸리티 함수
  function isValidEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  }
}
