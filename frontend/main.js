document.addEventListener("DOMContentLoaded", () => {

    var activeInterval = 1;

    function getInterval() {
        return activeInterval;
    }

    function setButtonInterval(button) {
        const interval = button.textContent.trim();
        if(interval.toLowerCase() === "yearly"){
            activeInterval = 12;
        }else{
            activeInterval = 1;
        }
    }

    const buttons = document.querySelectorAll("#interval-btn-display button");

        buttons.forEach(button => {
        button.addEventListener("click", () => {

            buttons.forEach(btn => btn.classList.remove("active"));

            button.classList.add("active");
            setButtonInterval(button);

        });
    });

    function getTickers(inputId) {
        
        const tickers = document.getElementById(inputId)
            .value
            .split(/[\s,]+/)
            .map(t => t.trim())
            .filter(Boolean);

        return [...new Set(tickers)];
    }

    function validate(tickers, responseId, rawInput) {
        const responseDiv = document.getElementById(responseId);

    if (!tickers || tickers.length === 0) {
        responseDiv.innerHTML =
            `<p style="color:red;">Error: you must enter all fields</p>`;
        return false;
    }

    return true;
    }


    function renderBetaList(responseId, title, items, valueKey) {
        const responseDiv = document.getElementById(responseId);

        responseDiv.innerHTML = `
            <div class="metric-card">
                <p>${title}</p>

                ${items.map(item => `
                    <div class="metric-card-row">
                        <span>${item.ticker.toUpperCase()}:</span>
                        <span>${item[valueKey]}</span>
                    </div>
                `).join("")}

                <button class="close-btn">Close Results</button>
            </div>
        `;

        responseDiv.querySelector(".close-btn").addEventListener("click", () => {
            responseDiv.innerHTML = "";
        });
    }

    function renderList(responseId, title, items, valueKey) {
        const responseDiv = document.getElementById(responseId);
        var interval;
        if(responseId === "standard-deviation-response")
            interval = Math.sqrt(getInterval());
        else
            interval = getInterval();
        if(getInterval() === 12){
            title = title.replace("Monthly", "Yearly");
        }
        responseDiv.innerHTML = `
            <div class="metric-card">
                <p>${title}</p>

                ${items.map(item => `
                    <div class="metric-card-row">
                        <span>${item.ticker.toUpperCase()}:</span>
                        <span>${(item[valueKey] * interval).toFixed(2)}%</span>
                    </div>
                `).join("")}

                <button class="close-btn">Close Results</button>
            </div>
        `;

        responseDiv.querySelector(".close-btn").addEventListener("click", () => {
            responseDiv.innerHTML = "";
        });
    }

    async function handleListRequest({
        formId,
        inputId,
        responseId,
        url,
        responseKey,
        title,
        mapResponse
    }) {
        document.getElementById(formId).addEventListener("submit", async (e) => {
            e.preventDefault();
            const rawInput = document.getElementById(inputId).value;

            const tickers = getTickers(inputId);

            if (!validate(tickers, responseId, rawInput)) return;

            const query = new URLSearchParams({
                tickers: tickers.join(",")
            });

            try {
                const res = await fetch(`${url}?${query}`);

                if (!res.ok) throw new Error("Server error");

                const data = await res.json();

                const items = mapResponse(data);

                renderList(responseId, title, items, responseKey);

                document.getElementById(inputId).value = "";

            } catch (err) {
                console.error(err);
                document.getElementById(responseId).innerHTML =
                    `<p style="color:red;">Error: ${err.message}</p>`;
            }
        });
    }

    async function handleBetaListRequest({
        formId,
        inputId,
        responseId,
        url,
        responseKey,
        title,
        mapResponse
    }) {
        document.getElementById(formId).addEventListener("submit", async (e) => {
            e.preventDefault();
            const rawInput = document.getElementById(inputId).value;

            const tickers = getTickers(inputId);

            if (!validate(tickers, responseId, rawInput)) return;

            const query = new URLSearchParams({
                tickers: tickers.join(",")
            });

            try {
                const res = await fetch(`${url}?${query}`);

                if (!res.ok) throw new Error("Server error");

                const data = await res.json();

                const items = mapResponse(data);

                renderBetaList(responseId, title, items, responseKey);

                document.getElementById(inputId).value = "";

            } catch (err) {
                console.error(err);
                document.getElementById(responseId).innerHTML =
                    `<p style="color:red;">Error: ${err.message}</p>`;
            }
        });
    }

    handleListRequest({
        formId: "expected-returns-form",
        inputId: "tickers-input",
        responseId: "expected-returns-response",
        url: "http://localhost:3010/api/expected-returns",
        title: "Monthly Expected Returns",
        responseKey: "expectedReturns",
        mapResponse: (data) =>
            data.tickers.map((ticker, i) => ({
                ticker,
                expectedReturns: (data.expectedReturns[i] * 100).toFixed(2)
            }))
    });

    handleListRequest({
        formId: "variance-form",
        inputId: "variance-tickers-input",
        responseId: "variance-response",
        url: "http://localhost:3010/api/variance",
        title: "Variance",
        responseKey: "variance",
        mapResponse: (data) =>
            data.tickers.map((ticker, i) => ({
                ticker,
                variance: (data.variance[i] * 100).toFixed(2)
            }))
    });

    handleListRequest({
        formId: "standard-deviation-form",
        inputId: "standard-deviation-tickers-input",
        responseId: "standard-deviation-response",
        url: "http://localhost:3010/api/standard-deviation",
        title: "Standard Deviation",
        responseKey: "standardDeviation",
        mapResponse: (data) =>
            data.tickers.map((ticker, i) => ({
                ticker,
                standardDeviation: (data.standardDeviation[i] * 100).toFixed(2)
            }))
    });

    handleBetaListRequest({
        formId: "beta-form",
        inputId: "beta-tickers-input",
        responseId: "beta-response",
        url: "http://localhost:3010/api/beta-stock",
        title: "Beta",
        responseKey: "beta",
        mapResponse: (data) =>
            data.tickers.map((ticker, i) => ({
                ticker,
                beta: (data.beta[i]).toFixed(2)
            }))
    });
});