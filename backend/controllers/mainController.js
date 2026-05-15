
/*
exports.portfolioTickers = async (req,res) => {
    const { investedArray , tickerArray } = req.body;
    
    if (!Array.isArray(tickerArray) || !Array.isArray(investedArray)) {
        return res.status(400).json({ error: "Expected an array" });
    } 

    if(tickerArray.length <= 1 || investedArray.length <= 1){
        return res.status(400).json({error: "to generate portfolios you must enter 2 or more tickers"});
    }

    if(tickerArray.length !== investedArray.length ){
        return res.status(400).json({error: "you must invest an amount to invest in every stock"});
    }

    const validNum = investedArray.every(
        amount => Number.isFinite(amount)
    );

    const validTic = tickerArray.every(
        ticker => typeof ticker === "string"
    );

    if(!validTic || !validNum){
        return res.status(400).json({error: "invalid input"});
    }

    //need to pass information to springboot;

};

exports.stockTickers = async (req, res) => {
    const {ticker} = req.body;

    if(ticker.length === 0){
        return res.status(400).json({error: "You must enter a tickers to see data"});
    }
    if(!(typeof ticker !== "string")){
        return res.status(400).json({error: "All tickers must be valid strings"});
    }

    //need to pass information to springboot;
};

exports.minimumVariancePortfolio= async (req, res) => {
    const { investedArray , tickerArray } = req.body;
    
    if (!Array.isArray(tickerArray) || !Array.isArray(investedArray)) {
        return res.status(400).json({ error: "Expected an array" });
    } 

    if(tickerArray.length <= 1 || investedArray.length <= 1){
        return res.status(400).json({error: "to generate portfolios you must enter 2 or more tickers"});
    }

    if(tickerArray.length !== investedArray.length ){
        return res.status(400).json({error: "you must invest an amount to invest in every stock"});
    }

    const validNum = investedArray.every(
        amount => Number.isFinite(amount)
    );

    const validTic = tickerArray.every(
        ticker => typeof ticker === "string"
    );

    if(!validTic || !validNum){
        return res.status(400).json({error: "invalid input"});
    }

    //need to pass information to springboot;
}

exports.tangencyPortfolio = async (req, res) => {
    const {tickers} = req.body;

}

*/