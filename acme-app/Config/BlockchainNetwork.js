var fabricClient = require('./FabricClient');
var FabricCAClient = require('fabric-ca-client');

class BlockchainNetwork {
  constructor(userName) {
    this.currentUser;
    this.issuer;
    this.userName = userName;
    this.connection = fabricClient;
  }
  init() {
    console.log('Start init');
    var isAdmin = false;
    if (this.userName == "admin") {
      isAdmin = true;
    }
    console.log('Username:'+this.userName);
    return this.connection.initCredentialStores().then(() => {
      return this.connection.getUserContext(this.userName, true)
    }).then((user) => {
      this.issuer = user;
      if (isAdmin) {
        return user;
      }
      return this.ping();
    }).then((user) => {
      this.currentUser = user;
      return user;
    })
  }
  getById(data) {
    console.log('Start getById');
    var tx_id = this.connection.newTransactionID();
    var requestData = {
      chaincodeId:'foodcontrol',
      fcn: 'query',
      args: [data.id],
      txId: tx_id
    };
    return this.connection.query(requestData);
  }
  set(data) {
    console.log('Start set');
    var tx_id = this.connection.newTransactionID();
    var requestData = {
      chaincodeId:'foodcontrol',
      fcn: 'set',
      args: [data.id,data.value],
      txId: tx_id
    };
    //var request = FabricModel.requestBuild(requestData);
    return this.connection.submitTransaction(requestData);

  }
}

var blockchanClient = new BlockchainNetwork('admin');
module.exports = blockchanClient;
