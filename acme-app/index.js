const express = require('express')
const app = express()
var bodyParser = require('body-parser')
var port = '8888'
var blockchanClient = require('./Config/BlockchainNetwork');


app.use( bodyParser.json() );


app.post('/api/getbyId', function(req, res) {
	blockchanClient.init().then(function(data) {
          return blockchanClient.getById(req.body)
        }).then(function (data) {
          res.status(200).json(data)
        }).catch(function(err) {
          res.status(500).json({error: err.toString()})
        })
})

app.post('/api/set', function(req, res) {
	blockchanClient.init().then(function(data) {
        	return blockchanClient.set(req.body)
        }).then(function (data) {
          res.status(200).json(data)
        }).catch(function(err) {
          res.status(500).json({error: err.toString()})
        })
})

app.listen(port)

