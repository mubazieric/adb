(function () {
  const searchTriggers = document.querySelectorAll('.iw-search-trigger');
  const modal = document.querySelector('.iw-search-modal');

  if (!modal) {
    return;
  }

  const input = modal.querySelector('input');
  const results = modal.querySelector('.iw-search-results');

  const openModal = () => {
    modal.classList.add('is-open');
    if (input) {
      input.focus();
    }
  };

  const closeModal = () => {
    modal.classList.remove('is-open');
  };

  searchTriggers.forEach((trigger) => {
    trigger.addEventListener('click', (event) => {
      event.preventDefault();
      openModal();
    });
  });

  modal.addEventListener('click', (event) => {
    if (event.target === modal) {
      closeModal();
    }
  });

  if (!input) {
    return;
  }

  let controller;

  input.addEventListener('input', () => {
    const term = input.value.trim();

    if (!term) {
      results.innerHTML = '';
      return;
    }

    if (controller) {
      controller.abort();
    }

    controller = new AbortController();

    results.innerHTML = `<span>${iwMarket.texts.searching}</span>`;

    const params = new URLSearchParams({
      action: 'iw_market_search',
      nonce: iwMarket.nonce,
      term,
    });

    fetch(`${iwMarket.ajaxUrl}?${params.toString()}`, {
      signal: controller.signal,
    })
      .then((response) => response.json())
      .then((data) => {
        if (!data.success) {
          results.innerHTML = '';
          return;
        }
        results.innerHTML = data.data
          .map((item) => `<a href="${item.url}">${item.title}</a>`)
          .join('');
      })
      .catch(() => {
        results.innerHTML = '';
      });
  });
})();
