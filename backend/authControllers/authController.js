require('dotenv').config();

console.log("JWT_SECRET loaded:", !!process.env.JWT_SECRET);

const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const accountModel = require("../models/accountModel");


const JWT_SECRET = process.env.JWT_SECRET;

exports.register = async (req,res) => {
    try{
    const {password, username, role} = req.body;
    
    if(!password || !username){
        return res.status(400).json({error: "missing fields"});
    }

    const finalRole = role || "guest";
    const hashedPassword = await bcrypt.hash(password, 10);

    const userId = await accountModel.createUser(username, hashedPassword, finalRole);
    
   
    res.status(200).json({ message: "Success", userId});
    
    }catch(err){
        console.log("Register error", err.message);
        return res.status(500).json({error: "Unexpected Server Error Please Try Again Later"});
    }
};

exports.login = async (req,res) => {
    try{
    const {username, password} = req.body;

    if(!username || !password){
        return res.status(400).json({ error: "Missing fields" });
    }
    const user = await accountModel.findByUsername(username);
       
    if(!user){
        return res.status(404).json({error: "account not found"});
    }
    const match = await bcrypt.compare(password, user.password);
    if(!match){
        return res.status(401).json({error: "Incorrect Password Entered"});
    }
    const token = jwt.sign(
        {
            id: user.id,
            username: user.username,
            role: user.role
        },
        JWT_SECRET,
        { expiresIn: "1h" }
    );

    return res.status(200).json({message: "logged in successfully", token});

    }catch(err){   
        return res.status(500).json({error: "Unexpected Server Error Please Try Again Later"});
   
    }
};

exports.deleteAccount = async (req, res) => {
    try{
    const {password} = req.body;
    const id = req.user.id;

    if (!password) {
    return res.status(400).json({ error: "Password is required" });
}
    
    const user = await accountModel.findByAccountId(id);

        if(!user){
            return res.status(404).json({error: "account not found"});
        }

        const match = await bcrypt.compare(password, user.password);

        if(!match){
            return res.status(401).json({error: "Incorrect Password Entered"});
        }
        const changes = await accountModel.deleteById(id);
        if (changes === 0) {
            return res.status(404).json({ error: "account not found" });
        }
        return res.status(200).json({message: "account deleted successfully"});

    }catch(err){
        return res.status(500).json({error: "Unexpected Server Error Please Try Again Later"}); 
    }
};