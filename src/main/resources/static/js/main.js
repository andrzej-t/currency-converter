const API_BASE_URL = 'http://localhost:8080/v1';

const amountInput = document.getElementById('amount');
const convertBtn = document.getElementById('convertBtn');
const resultDisplay = document.getElementById('result');
const converterForm = document.getElementById('converterForm');
const ratesContainer = document.getElementById('ratesContainer');

const customCurrencyFromDiv = document.getElementById('customCurrencyFrom');
const customCurrencyFromSelected = customCurrencyFromDiv.querySelector('.select-selected');
const customCurrencyFromItems = customCurrencyFromDiv.querySelector('.select-items');
const customCurrencyFromValueInput = document.getElementById('currencyFromValue');

const customCurrencyToDiv = document.getElementById('customCurrencyTo');
const customCurrencyToSelected = customCurrencyToDiv.querySelector('.select-selected');
const customCurrencyToItems = customCurrencyToDiv.querySelector('.select-items');
const customCurrencyToValueInput = document.getElementById('currencyToValue');


function updateConvertButton() {
    const isValid = amountInput.value &&
        customCurrencyFromValueInput.value &&
        customCurrencyToValueInput.value;
    convertBtn.disabled = !isValid;
}

amountInput.addEventListener('input', updateConvertButton);

converterForm.addEventListener('submit', async (e) => {
    e.preventDefault();

    const amount = parseFloat(amountInput.value);
    const currencyFrom = customCurrencyFromValueInput.value;
    const currencyTo = customCurrencyToValueInput.value;

    if (isNaN(amount) || !currencyFrom || !currencyTo) {
        alert('Uzupełnij wszystkie pola przed przeliczeniem');
        return;
    }

    try {
        convertBtn.disabled = true;
        convertBtn.textContent = 'Przeliczanie...';

        const response = await fetch(
            `${API_BASE_URL}/result?amount=${amount}&currencyFrom=${currencyFrom}&currencyTo=${currencyTo}`
        );

        if (!response.ok) {
            console.error('API Error:', response.status, response.statusText);
            showError('Błąd podczas przeliczania waluty');
            return;
        }

        const result = await response.json();
        resultDisplay.textContent = result.toFixed(2) + ' ' + currencyTo;

    } catch (error) {
        console.error('Error:', error);
        resultDisplay.textContent = 'Błąd';
        showError('Nie udało się przeliczyć waluty. Sprawdź czy backend działa.');
    } finally {
        convertBtn.textContent = 'Przelicz';
        updateConvertButton();
    }
});

async function loadCurrencyRates() {
    try {
        const response = await fetch(`${API_BASE_URL}/currencies`);

        if (!response.ok) {
            console.error('API Error:', response.status, response.statusText);
            ratesContainer.innerHTML = `
        <div class="system-message-error">
          Nie udało się pobrać kursów walut.
          Upewnij się, że backend działa na adresie ${API_BASE_URL}
        </div>
      `;
            return;
        }

        const rates = await response.json();
        displayRates(rates);

    } catch (error) {
        console.error('Error:', error);
        ratesContainer.innerHTML = `
      <div class="system-message-error">
        Nie udało się pobrać kursów walut.
        Upewnij się, że backend działa na adresie ${API_BASE_URL}
      </div>
    `;
    }
}

function displayRates(rates) {
    if (!rates || rates.length === 0) {
        ratesContainer.innerHTML = '<div class="loading">Brak danych</div>';
        return;
    }

    ratesContainer.innerHTML = `
    <div class="rates-table-container">
      <table>
        <thead>
          <tr>
            <th>Nazwa waluty</th>
            <th>Symbol waluty</th>
            <th>Kupno</th>
            <th>Sprzedaż</th>
          </tr>
        </thead>
        <tbody>
          ${rates.map(rate => `
            <tr>
              <td>${rate.currency || '-'}</td>
              <td>${rate.code || '-'}</td>
              <td>${rate.bid ? rate.bid.toFixed(4) : '-'}</td>
              <td>${rate.ask ? rate.ask.toFixed(4) : '-'}</td>
            </tr>
          `).join('')}
        </tbody>
      </table>
    </div>
  `;
}

function showError(message) {
    const existingError = document.querySelector('.converter-section > .system-message-error');
    if (existingError) {
        existingError.remove();
    }

    const errorDiv = document.createElement('div');
    errorDiv.className = 'system-message-error';
    errorDiv.textContent = message;
    converterForm.insertAdjacentElement('afterend', errorDiv);

    setTimeout(() => errorDiv.remove(), 5000);
}

function closeAllSelect(clickedSelectedDiv = null) {
    const allSelectItems = document.querySelectorAll(".custom-select .select-items");
    const allSelectedDivs = document.querySelectorAll(".custom-select .select-selected");

    allSelectedDivs.forEach(selectedDiv => {
        if (selectedDiv !== clickedSelectedDiv) {
            selectedDiv.classList.remove("select-arrow-active");
        }
    });

    allSelectItems.forEach(itemsDiv => {
        if (itemsDiv.classList.contains("select-hide") === false && itemsDiv.previousElementSibling !== clickedSelectedDiv) {
            itemsDiv.classList.add("select-hide");
        }
    });
}

function setupCustomSelect(customSelectDiv, selectedDiv, itemsDiv, valueInput) {
    selectedDiv.addEventListener('click', function(e) {
        e.stopPropagation();
        closeAllSelect(this);
        this.classList.toggle("select-arrow-active");
        itemsDiv.classList.toggle("select-hide");
    });

    for (let i = 0; i < itemsDiv.children.length; i++) {
        itemsDiv.children[i].addEventListener('click', function() {
            const selectedText = this.textContent;
            const selectedValue = this.getAttribute('data-value');

            selectedDiv.textContent = selectedText;
            valueInput.value = selectedValue;

            const currentSelected = itemsDiv.querySelector('.same-as-selected');
            if (currentSelected) {
                currentSelected.classList.remove('same-as-selected');
            }
            this.classList.add('same-as-selected');

            selectedDiv.click();
            updateConvertButton();
        });
    }

    selectedDiv.textContent = itemsDiv.children[0].textContent;
    valueInput.value = itemsDiv.children[0].getAttribute('data-value');
    itemsDiv.children[0].classList.add('same-as-selected');
}


document.addEventListener('DOMContentLoaded', () => {
    loadCurrencyRates().catch(error => {
        console.error("Unhandled promise rejection from loadCurrencyRates:", error);
    });
    setupCustomSelect(customCurrencyFromDiv, customCurrencyFromSelected, customCurrencyFromItems, customCurrencyFromValueInput);
    setupCustomSelect(customCurrencyToDiv, customCurrencyToSelected, customCurrencyToItems, customCurrencyToValueInput);
    updateConvertButton();
});

document.addEventListener('click', function(e) {
    const clickedCustomSelect = e.target.closest('.custom-select') ? e.target.closest('.custom-select').querySelector('.select-selected') : null;
    closeAllSelect(clickedCustomSelect);
});