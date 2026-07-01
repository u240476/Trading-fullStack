const db = require("../config/db");

// CREATE USER
exports.createUser = (username, password, role) => {
    return new Promise((resolve, reject) => {
        const sql = "INSERT INTO accounts (password, username, role) VALUES (?, ?, ?)";
        db.run(sql, [password, username, role], function(err) {
            if(err){
                reject(err);
            }else{
                resolve(this.lastID);
            }
        });
    });
};

// FIND USER BY USERNAME
exports.findByUsername = (username) => {
    return new Promise((resolve, reject) => {
        const sql = "SELECT * FROM accounts WHERE username = ?";
        db.get(sql, [username], (err,row) => {
            if(err){
                reject(err);
            }else{
                resolve(row);
            }
        });
    });
};

// FIND USER BY ID
exports.findByAccountId = (id) => {
    return new Promise((resolve, reject) => {
        const sql = "SELECT * FROM accounts WHERE id = ?";
        db.get(sql, [id], (err, row) => {
            if(err){
                reject(err);
            }else{
                resolve(row);
            }
        });
    });
};

// DELETE USER
exports.deleteById = (id) => {
    return new Promise((resolve, reject) => {
        const sql = "DELETE FROM accounts WHERE id = ?";
        db.run(sql, [id], (err,row) => {
            if(err){
                reject(err);
            }else{
                resolve(this.changes);
            }
        });
    });
};






