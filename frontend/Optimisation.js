document.addEventListener("DOMContentLoaded", () => {

    const mvpForm = document.getElementById("mvp-form");
    const tpForm = document.getElementById("tp-form");

    const mvpInput = document.getElementById("mvp-tickers-input");
    const tpInput = document.getElementById("tp-tickers-input");

    const mvpResponse = document.getElementById("mvp-response");
    const tpResponse = document.getElementById("tp-response");


    function parseTickers(input) {
        return [...new Set(
            input.split(",")
                .map(t => t.trim().toUpperCase())
                .filter(Boolean)
        )];
    }

    function renderWeights(container, data) {
        const combined = data.tickers.map((ticker, i) => ({
            ticker,
            weight: Number((data.weights[i] * 100).toFixed(2))
        }));

        container.innerHTML = `
            <div class="metric-card">
                <p>Weights</p>
                ${combined.map(item => `
                    <div class="metric-card-row">
                        <span>${item.ticker}:</span>
                        <span>${item.weight}%</span>
                    </div>
                `).join("")}
                <button class="close-btn">Close Results</button>
            </div>
        `;

        container.querySelector(".close-btn").addEventListener("click", () => {
            container.innerHTML = "";
        });
    }

    async function fetchPortfolio(type, tickers) {
        const query = new URLSearchParams({
            tickers: tickers.join(",")
        });

        const res = await fetch(`http://localhost:3010/api/${type}?${query}`);

        if (!res.ok) {
            throw new Error("Server error");
        }

        return await res.json();
    }

    function showError(container, message) {
        container.innerHTML = `<p style="color:red;">Error: ${message}</p>`;
    }


    mvpForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const tickers = parseTickers(mvpInput.value);

        if (tickers.length === 0) {
            showError(mvpResponse, "You must enter at least one ticker");
            return;
        }

        try {
            const data = await fetchPortfolio("mvp", tickers);
            renderWeights(mvpResponse, data);
        } catch (err) {
            console.error(err);
            showError(mvpResponse, err.message);
        }
    });


    tpForm.addEventListener("submit", async (e) => {
        e.preventDefault();

        const tickers = parseTickers(tpInput.value);

        if (tickers.length === 0) {
            showError(tpResponse, "You must enter at least one ticker");
            return;
        }

        try {
            const data = await fetchPortfolio("tp", tickers);
            renderWeights(tpResponse, data);
        } catch (err) {
            console.error(err);
            showError(tpResponse, err.message);
        }
    });

});