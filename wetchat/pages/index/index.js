const util = require('../../utils/util.js');

Page({
  data: {
    medications: []
  },

  onShow: function() {
    this.loadMedications();
    this.checkReminders();
  },

  loadMedications: function() {
    const medications = wx.getStorageSync('medications') || [];
    this.setData({ medications });
  },

  checkReminders: function() {
    const now = new Date();
    const currentTime = now.getHours() * 100 + now.getMinutes();
    const today = util.formatDate(now);

    this.data.medications.forEach(med => {
      Object.keys(med.slots).forEach(slot => {
        if (med.slots[slot].time) {
          const [hour, minute] = med.slots[slot].time.split(':').map(Number);
          const slotTime = hour * 100 + minute;
          const reminderKey = `reminder_${med.id}_${slot}_${today}`;

          if (Math.abs(currentTime - slotTime) <= 5 && !wx.getStorageSync(reminderKey)) {
            this.showReminder(med, slot);
            wx.setStorageSync(reminderKey, true);
          }
        }
      });
    });
  },

  showReminder: function(med, slot) {
    const slotName = { morning: '早上', noon: '中午', evening: '晚上' }[slot];
    if (med.reminderType === 'popup') {
      wx.showModal({
        title: '用药提醒',
        content: `该吃${med.name}了！${slotName} ${med.dosage} ${med.beforeMeal ? '饭前' : '饭后'}`,
        showCancel: true,
        confirmText: '已服用',
        cancelText: '稍后',
        success: (res) => {
          if (res.confirm) {
            this.recordTaken(med.id, slot);
          }
        }
      });
    } else {
      // 铃声提醒（需添加音频文件）
      wx.showToast({ title: '铃声提醒功能待实现', icon: 'none' });
    }
  },

  recordTaken: function(medId, slot) {
    let medications = wx.getStorageSync('medications') || [];
    medications = medications.map(med => {
      if (med.id === medId && med.tracking) {
        if (!med.takenHistory) med.takenHistory = [];
        med.takenHistory.push({
          date: util.formatDate(new Date()),
          slot: slot,
          time: util.formatTime(new Date())
        });
      }
      return med;
    });
    wx.setStorageSync('medications', medications);
    this.loadMedications();
  },

  getNextReminder: function(med) {
    const now = new Date();
    let nextTime = null;
    Object.keys(med.slots).forEach(slot => {
      if (med.slots[slot].time) {
        const [hour, minute] = med.slots[slot].time.split(':');
        const reminderTime = new Date();
        reminderTime.setHours(hour, minute, 0, 0);
        if (reminderTime > now && (!nextTime || reminderTime < nextTime)) {
          nextTime = reminderTime;
        }
      }
    });
    return nextTime ? util.formatTime(nextTime) : '今日已提醒';
  },

  onTake: function(e) {
    const medId = e.currentTarget.dataset.id;
    const med = this.data.medications.find(m => m.id === medId);
    wx.showActionSheet({
      itemList: Object.keys(med.slots).filter(slot => med.slots[slot].time).map(slot => {
        return { morning: '早上', noon: '中午', evening: '晚上' }[slot];
      }),
      success: (res) => {
        const slotKeys = Object.keys(med.slots);
        this.recordTaken(medId, slotKeys[res.tapIndex]);
      }
    });
  },

  onEdit: function(e) {
    const medId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/add-med/add-med?id=${medId}`
    });
  },

  onAdd: function() {
    wx.navigateTo({
      url: '/pages/add-med/add-med'
    });
  }
});