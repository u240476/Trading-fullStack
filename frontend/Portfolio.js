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


    function getTickersAndProportions(tickerInputId, propInputId) {
        const tickers = document.getElementById(tickerInputId)
            .value
            .split(/[\s,]+/)
            .map(t => t.trim())
            .filter(Boolean);

        const proportions = document.getElementById(propInputId)
            .value
            .split(/[\s,]+/)
            .map(t => t.trim())
            .filter(Boolean);

        return {
            tickers: [...new Set(tickers)],
            proportions: proportions
        };
    }

    function validateInputs(tickers, proportions, responseId, rawTickerInput) {
        const responseDiv = document.getElementById(responseId);

        if (tickers.length === 0) {
            responseDiv.innerHTML = `<p style="color:red;">Error: you must enter all fields</p>`;
            return false;
        }

        if (proportions.length !== tickers.length) {
            responseDiv.innerHTML = `<p style="color:red;">Error: must have one amount invested per ticker</p>`;
            return false;
        }

        return true;
    }

    function renderResult(responseId, label, value) {
        const responseDiv = document.getElementById(responseId);
        if(getInterval() === 12){
            label = label.replace("Monthly", "Yearly");
        }
        responseDiv.innerHTML = `
            <div class="metric-card">
                <p>${label}</p>

                <div class="metric-card-row">
                    <span>${value}%</span>
                </div>

                <button class="close-btn">Close Results</button>
            </div>
        `;

        responseDiv.querySelector(".close-btn").addEventListener("click", () => {
            responseDiv.innerHTML = "";
        });
    }

    async function handlePortfolioRequest({
        formId,
        tickerInputId,
        propInputId,
        responseId,
        url,
        responseKey,
        label,
        multiplier = 100
    }) {
        document.getElementById(formId).addEventListener("submit", async (e) => {
            e.preventDefault();
            const rawTickerInput = document.getElementById(tickerInputId).value;
           
            const { tickers, proportions } =
                getTickersAndProportions(tickerInputId, propInputId);

            if (!validateInputs(tickers, proportions, responseId, rawTickerInput)) return;

            const query = new URLSearchParams({
                tickers: tickers.join(","),
                proportions: proportions.join(",")
            });

            try {
                const res = await fetch(`${url}?${query}`);

                if (!res.ok) throw new Error("Server error");

                const data = await res.json();
                var interval;
                if(formId === "portfolio-standard-deviation-form")
                    interval = Math.sqrt(getInterval());
                else
                    interval = getInterval();


                const value = Number((data[responseKey] * multiplier * interval).toFixed(2));

                renderResult(responseId, label, value);

                document.getElementById(tickerInputId).value = "";
                document.getElementById(propInputId).value = "";

            } catch (err) {
                console.error(err);
                document.getElementById(responseId).innerHTML =
                    `<p style="color:red;">Error: ${err.message}</p>`;
            }
        });
    }


    handlePortfolioRequest({
        formId: "expected-portfolio-returns-form",
        tickerInputId: "portfolio-returns-tickers-input",
        propInputId: "portfolio-returns-proportions-input",
        responseId: "expected-portfolio-returns-response",
        url: "http://localhost:3010/api/expected-portfolio-returns",
        responseKey: "expectedReturns",
        label: "Expected Monthly Return"
    });

    handlePortfolioRequest({
        formId: "portfolio-variance-form",
        tickerInputId: "portfolio-variance-tickers-input",
        propInputId: "portfolio-variance-proportions-input",
        responseId: "portfolio-variance-response",
        url: "http://localhost:3010/api/portfolio-variance",
        responseKey: "portfolioVariance",
        label: "Monthly Variance"
    });

    handlePortfolioRequest({
        formId: "portfolio-standard-deviation-form",
        tickerInputId: "portfolio-standard-deviation-tickers-input",
        propInputId: "portfolio-standard-deviation-proportions-input",
        responseId: "portfolio-standard-deviation-response",
        url: "http://localhost:3010/api/portfolio-standard-deviation",
        responseKey: "portfolioStandardDeviation",
        label: "Monthly Standard Deviation"
    });

});