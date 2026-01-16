// Hàm tiện ích để bật/tắt loading cho một khu vực cụ thể
function setLoading(isLoading, selectId, spinnerId) {
    const select = document.getElementById(selectId);
    const spinner = document.getElementById(spinnerId);

    if (isLoading) {
        spinner.classList.remove('d-none'); // Hiện spinner
        select.disabled = true;             // Khóa ô chọn
    } else {
        spinner.classList.add('d-none');    // Ẩn spinner
        select.disabled = false;            // Mở ô chọn
    }
}

async function fetchJSON(url, options) {
    const res = await fetch(url, options || {});
    if (!res.ok) throw new Error('HTTP ' + res.status);
    return await res.json();
}

// --- CẬP NHẬT HÀM LOAD QUỐC GIA ---
async function loadCountries() {
    const countrySelect = document.getElementById('countrySelect');

    // Bật loading
    setLoading(true, 'countrySelect', 'countrySpinner');
    countrySelect.innerHTML = '<option value="">-- Loading data... --</option>';

    try {
        const countries = await fetchJSON('/api/countries');
        countries.sort((a, b) => (a.name || '').localeCompare(b.name || ''));

        // Xóa text "Đang tải" và thêm option mặc định
        countrySelect.innerHTML = '<option value="">-- Select a country --</option>';

        for (const c of countries) {
            const opt = document.createElement('option');
            opt.value = c.code;
            opt.textContent = c.name + (c.code ? ' (' + c.code + ')' : '');
            countrySelect.appendChild(opt);
        }
    } catch (e) {
        console.error('Failed to load countries', e);
        countrySelect.innerHTML = '<option value="">Error loading data</option>';
    } finally {
        // Tắt loading dù thành công hay thất bại
        setLoading(false, 'countrySelect', 'countrySpinner');
    }
}

// --- CẬP NHẬT HÀM LOAD SÂN BAY ---
async function loadAirportsForCountry(countryCode) {
    const airportSelect = document.getElementById('airportSelect');
    const helper = document.getElementById('airportHelper');

    // Reset và Bật loading
    helper.textContent = '';
    airportSelect.innerHTML = '<option value="">-- Loading airports... --</option>';
    setLoading(true, 'airportSelect', 'airportSpinner');

    try {
        let airports = await fetchJSON('/api/airports?country_code=' + encodeURIComponent(countryCode));

        airportSelect.innerHTML = '<option value="">-- Select an airport --</option>';
        airports.sort((a, b) => (a.name || '').localeCompare(b.name || ''));

        for (const a of airports) {
            const opt = document.createElement('option');
            opt.value = a.iataCode;
            const code = a.iataCode ? a.iataCode : (a.icaoCode || '');
            opt.textContent = (a.name || code) + (code ? ' (' + code + ')' : '');
            airportSelect.appendChild(opt);
        }

        if (airports.length === 0) {
            helper.textContent = 'No airports found for this country.';
        } else {
            helper.textContent = ''; // Xóa thông báo cũ nếu thành công
        }

    } catch (e) {
        airportSelect.innerHTML = '<option value="">-- Error loading airports --</option>';
        helper.textContent = 'An error occurred while loading the airport list.';
        console.error('Failed to load airports', e);
    } finally {
        // Tắt loading
        setLoading(false, 'airportSelect', 'airportSpinner');
    }
}

// Các hàm khác giữ nguyên (onCountryChange, renderFlights, onSearch, event listener...)
function onCountryChange(ev) {
    const countryCode = ev.target.value;
    const airportSelect = document.getElementById('airportSelect');
    airportSelect.innerHTML = '<option value="">-- Please select a country first --</option>';
    if (countryCode) {
        loadAirportsForCountry(countryCode);
    }
}

function renderFlights(flights) {
  const container = document.getElementById('flightsContainer');
  container.innerHTML = '';

  const card = document.createElement('div');
  card.className = 'card p-4';

  if (!flights || flights.length === 0) {
    const warn = document.createElement('div');
    warn.className = 'alert alert-warning';
    warn.textContent = 'No flights found or API rate limit exceeded.';
    card.appendChild(warn);
    container.appendChild(card);
    return;
  }

  const table = document.createElement('table');
  table.className = 'table table-striped table-hover';
  table.innerHTML = `
    <thead>
      <tr>
        <th>Airline</th>
        <th>Flight (IATA)</th>
        <th>Departure Time</th>
        <th>Departure Time UTC</th>
        <th>Arrival (IATA)</th>
        <th>Arrival Time</th>
        <th>Arrival Time UTC</th>
        <th>Status</th>
      </tr>
    </thead>
    <tbody></tbody>`;

  const tbody = table.querySelector('tbody');

  const fmt = (dt) => {
    if (!dt) return '-';
    try {
      const d = new Date(dt);
      return d.toLocaleString('vi-VN', {
        hour: '2-digit',
        minute: '2-digit',
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      });
    } catch (_) {
      return '-';
    }
  };

  for (const f of flights) {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${f.airlineName ?? f.airlineIata ?? '-'}</td>
      <td>${f.flightIata ?? '-'}</td>
      <td>${fmt(f.depTime)}</td>
      <td>${fmt(f.depTimeUtc)}</td>
      <td>${f.arrAirportName ?? '-'}</td>
      <td>${fmt(f.arrTime)}</td>
      <td>${fmt(f.arrTimeUtc)}</td>
      <td>
        <span class="badge ${
          f.status === 'active' || f.status === 'landed'
            ? 'bg-success'
            : 'bg-secondary'
        }">
          ${f.status ?? '-'}
        </span>
      </td>
    `;
    tbody.appendChild(tr);
  }

  card.appendChild(table);
  container.appendChild(card);
}

async function onSearch(e) {
    e.preventDefault(); // ❗ cực kỳ quan trọng

    const airportCode = document.getElementById('airportSelect').value;
    if (!airportCode) {
        alert('Please select an airport');
        return;
    }

    try {
        console.log('🔍 Fetching flights for:', airportCode);
        const flights = await fetchJSON(
            '/api/flights?airport_code=' + encodeURIComponent(airportCode)
        );
        renderFlights(flights);
    } catch (err) {
        console.error('Failed to load flights', err);
        renderFlights([]);
    }
}

document.addEventListener('DOMContentLoaded', () => {
    loadCountries();

    const countrySelect = document.getElementById('countrySelect');
    countrySelect.addEventListener('change', onCountryChange);

    const form = document.getElementById('searchForm');
    if (form) {
        form.addEventListener('submit', onSearch);
    } else {
        console.error('❌ searchForm not found');
    }
});

