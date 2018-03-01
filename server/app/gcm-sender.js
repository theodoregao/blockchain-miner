const gcm = require('node-gcm');

const { GCM_SERVER_API_KEY } = require('../config');

const sender = new gcm.Sender(GCM_SERVER_API_KEY);

class GcmSender {
  notifyDone(clientIds, succeedJobId) {
    sender.send(
      new gcm.Message({
        data: {
          message: {
            event: 'done',
            jobId: succeedJobId
          }
        }
      }),
      clientIds,
      (err, res) => {
        if (err) {
          console.log('gcm send message error ', err);
          return;
        }
        console.log('gcm send message succeed', res);
      }
    );

    this.notifyPeerCountUpdated(clientIds, 0);
  }

  notifyPeerCountUpdated(clientIds, peerCount) {
    sender.send(
      new gcm.Message({
        data: {
          message: {
            event: 'peer_count_updated',
            peer_count: peerCount
          }
        }
      }),
      clientIds,
      (err, res) => {
        if (err) {
          console.log('gcm send message error ', err);
          return;
        }
        console.log('gcm send message succeed', res);
      }
    );
  }
}

module.exports = GcmSender;
