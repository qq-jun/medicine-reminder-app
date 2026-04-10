// logs.js
const util = require('../../utils/util.js')

Component({
  data: {
    history: []
  },
  lifetimes: {
    attached() {
      const medications = wx.getStorageSync('medications') || [];
      const history = [];
      medications.forEach(med => {
        if (med.takenHistory) {
          med.takenHistory.forEach(record => {
            history.push({
              medName: med.name,
              date: record.date,
              slot: { morning: '早上', noon: '中午', evening: '晚上' }[record.slot],
              time: record.time
            });
          });
        }
      });
      history.sort((a, b) => new Date(b.date + ' ' + b.time) - new Date(a.date + ' ' + a.time));
      this.setData({ history });
    }
  },
})
