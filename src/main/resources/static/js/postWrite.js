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
  
}


