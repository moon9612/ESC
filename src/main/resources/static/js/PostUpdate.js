// 체크박스
function toggleCheckbox(element) {
    element.classList.toggle("checked");
  }
  
  window.onload = function () {
    // 드롭다운
    document.getElementById("category-button").addEventListener("click", () => {
      document.querySelector(".custom-select").classList.toggle("open");
    });
  
    document.querySelectorAll(".custom-select-option").forEach(option => {
      option.addEventListener("click", () => {
        const selected = document.getElementById("selected-category");
        selected.textContent = option.textContent.trim();
        document.querySelector(".custom-select").classList.remove("open");
      });
    });
    document.querySelectorAll('.custom-select-option').forEach(option => {
      option.addEventListener('click', function () {
        const value = this.getAttribute('data-value');
        const label = this.textContent;
  
        document.getElementById('selected-category').textContent = label;
        document.getElementById('category').value = value;
      });
    });
  
  
    // 파일 선택 시 처리하는 함수
    document.getElementById('file-upload').addEventListener('change', function (event) {
      const fileList = document.getElementById('file-list');
      const fileContentHead = document.querySelector('.file-content-head'); // file-content-head 요소
      const uploadText = document.querySelector('.file-content-head p'); // 업로드 텍스트 요소
  
      // 기존에 선택된 파일 목록 초기화
      fileList.innerHTML = '';
  
      // 파일이 선택되었으면 file-content-head 숨기기
      fileContentHead.style.display = 'none';
  
      const file = event.target.files[0]; // 하나의 파일만 선택 가능하므로 첫 번째 파일만 사용
  
      if (file) {
        const fileItem = document.createElement('div');
        fileItem.classList.add('file-item');
    
        const fileName = document.createElement('span');
        fileName.classList.add('file-name');
        fileName.textContent = file.name; // 파일 이름 표시
  
        // 이미지 파일일 경우 미리보기 처리
        if (file.type.startsWith('image/')) {
          const reader = new FileReader();
          reader.onload = function (e) {
            const img = document.createElement('img');
            img.src = e.target.result;
            img.classList.add('file-preview');
            fileItem.appendChild(img);
          };
          reader.readAsDataURL(file);
        }
  
        fileItem.appendChild(fileName);
        fileList.appendChild(fileItem); // 업로드된 파일을 file-upload-area 내부에 추가
      }
    });
  }
  
  document.addEventListener("DOMContentLoaded", function () {
    const options = document.querySelectorAll(".custom-select-option");
    const selectedCategory = document.getElementById("selected-category");
    const hiddenInput = document.getElementById("post_category");
  
    options.forEach(option => {
      option.addEventListener("click", function () {
        const value = this.getAttribute("data-value");
        selectedCategory.textContent = value;
        hiddenInput.value = value;
      });
    });
  });
  
  
  