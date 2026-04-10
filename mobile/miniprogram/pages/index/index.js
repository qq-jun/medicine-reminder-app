Page({
  data: {
    records: [],
    editingId: null,
    form: {
      name: '',
      dose: '',
      meal: '饭前',
      condition: '',
      remark: '',
      repeatType: 'daily1',
      intervalDays: 1,
      repeatWeekdays: [],
      timePeriods: [],
      periodTimes: {
        morning: '',
        noon: '',
        evening: ''
      },
      tone: 'default',
      volume: 0.5
    },
    repeatOptions: ['每天1次', '每天2次（早、晚）', '每天3次（早、中、晚）', '间隔X天', '特定星期'],
    repeatIndex: 0,
    mealOptions: ['饭前', '饭后'],
    mealIndex: 0,
    toneOptions: ['默认', '提示1', '提示2', '静音'],
    toneIndex: 0,
    periods: [
      { value: 'morning', label: '早上' },
      { value: 'noon', label: '中午' },
      { value: 'evening', label: '晚上' }
    ],
    weekdays: [
      { value: 1, label: '一' },
      { value: 2, label: '二' },
      { value: 3, label: '三' },
      { value: 4, label: '四' },
      { value: 5, label: '五' },
      { value: 6, label: '六' },
      { value: 0, label: '日' }
    ]
  },

  onLoad: function () {
    this.loadRecords();
    this.checkReminders();
  },

  loadRecords: function () {
    const records = wx.getStorageSync('medicineRecords') || [];
    this.setData({ records });
  },

  saveRecords: function () {
    wx.setStorageSync('medicineRecords', this.data.records);
  },

  onInputChange: function (e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({
      [`form.${field}`]: value
    });
  },

  onRepeatChange: function (e) {
    const index = e.detail.value;
    const repeatTypeMap = ['daily1', 'daily2', 'daily3', 'interval', 'weekly'];
    const repeatType = repeatTypeMap[index];
    this.setData({
      repeatIndex: index,
      'form.repeatType': repeatType
    });
    this.updateFormUI();
  },

  onMealChange: function (e) {
    const index = e.detail.value;
    const meal = this.data.mealOptions[index];
    this.setData({
      mealIndex: index,
      'form.meal': meal
    });
  },

  onToneChange: function (e) {
    const index = e.detail.value;
    const toneMap = ['default', 'tone1', 'tone2', 'none'];
    const tone = toneMap[index];
    this.setData({
      toneIndex: index,
      'form.tone': tone
    });
  },

  onVolumeChange: function (e) {
    this.setData({
      'form.volume': e.detail.value
    });
  },

  onWeekdayChange: function (e) {
    this.setData({
      'form.repeatWeekdays': e.detail.value.map(Number)
    });
  },

  onPeriodChange: function (e) {
    const periods = e.detail.value;
    this.setData({
      'form.timePeriods': periods
    });
    this.updateFormUI();
  },

  onTimeChange: function (e) {
    const period = e.currentTarget.dataset.period;
    const time = e.detail.value;
    this.setData({
      [`form.periodTimes.${period}`]: time
    });
  },

  updateFormUI: function () {
    const { repeatType, timePeriods } = this.data.form;
    // 根据repeatType调整timePeriods
    if (repeatType === 'daily1') {
      // 允许选择一个
    } else if (repeatType === 'daily2') {
      this.setData({
        'form.timePeriods': ['morning', 'evening']
      });
    } else if (repeatType === 'daily3') {
      this.setData({
        'form.timePeriods': ['morning', 'noon', 'evening']
      });
    }
  },

  addOrUpdate: function () {
    const form = this.data.form;
    if (!form.name || !form.timePeriods.length) {
      wx.showToast({
        title: '请填写药品名称并选择至少一个时间段',
        icon: 'none'
      });
      return;
    }

    const record = {
      ...form,
      createdAt: Date.now(),
      remindedDates: {
        morning: null,
        noon: null,
        evening: null,
      },
    };

    const records = [...this.data.records];
    if (this.data.editingId !== null) {
      records[this.data.editingId] = { ...records[this.data.editingId], ...record };
      wx.showToast({ title: '记录已更新' });
    } else {
      records.push(record);
      wx.showToast({ title: '已添加用药记录' });
    }

    this.setData({ records });
    this.saveRecords();
    this.clearForm();
  },

  clearForm: function () {
    this.setData({
      editingId: null,
      form: {
        name: '',
        dose: '',
        meal: '饭前',
        condition: '',
        remark: '',
        repeatType: 'daily1',
        intervalDays: 1,
        repeatWeekdays: [],
        timePeriods: [],
        periodTimes: {
          morning: '',
          noon: '',
          evening: ''
        },
        tone: 'default',
        volume: 0.5
      },
      repeatIndex: 0,
      mealIndex: 0,
      toneIndex: 0
    });
  },

  editRecord: function (e) {
    const index = e.currentTarget.dataset.index;
    const rec = this.data.records[index];
    const repeatTypeMap = { daily1: 0, daily2: 1, daily3: 2, interval: 3, weekly: 4 };
    const toneMap = { default: 0, tone1: 1, tone2: 2, none: 3 };
    this.setData({
      editingId: index,
      form: { ...rec },
      repeatIndex: repeatTypeMap[rec.repeatType] || 0,
      mealIndex: this.data.mealOptions.indexOf(rec.meal),
      toneIndex: toneMap[rec.tone] || 0
    });
  },

  deleteRecord: function (e) {
    const index = e.currentTarget.dataset.index;
    wx.showModal({
      title: '确认删除',
      content: '确定删除该用药记录吗？',
      success: (res) => {
        if (res.confirm) {
          const records = [...this.data.records];
          records.splice(index, 1);
          this.setData({ records });
          this.saveRecords();
          wx.showToast({ title: '记录已删除' });
        }
      }
    });
  },

  triggerReminder: function (e) {
    const index = e.currentTarget.dataset.index;
    const rec = this.data.records[index];
    this.playReminder(rec);
  },

  playReminder: function (rec) {
    const message = `${rec.name} ${rec.dose}，${rec.meal}服用，主治${rec.condition}`;
    wx.showToast({
      title: message,
      icon: 'none',
      duration: 3000
    });
    wx.vibrateShort({ type: 'light' });
    if (rec.tone !== 'none') {
      this.playTone(rec.tone, rec.volume);
    }
  },

  playTone: function (tone, volume) {
    const audio = wx.createInnerAudioContext();
    // 小程序音频播放，需要预设音频文件，这里简化用系统音
    // 实际需要上传音频文件到小程序
    wx.showToast({ title: '播放提醒音', icon: 'none' });
  },

  checkReminders: function () {
    const now = new Date();
    const todayKey = now.toISOString().split('T')[0];
    const records = this.data.records.map(rec => {
      if (this.isTodayEligible(rec, now)) {
        rec.timePeriods.forEach(period => {
          if (rec.remindedDates[period] !== todayKey) {
            // 检查时间
            const time = rec.periodTimes[period];
            if (time) {
              const [h, m] = time.split(':').map(Number);
              const reminderTime = new Date(now.getFullYear(), now.getMonth(), now.getDate(), h, m);
              if (now >= reminderTime) {
                this.playReminder(rec);
                rec.remindedDates[period] = todayKey;
              }
            }
          }
        });
      }
      return rec;
    });
    this.setData({ records });
    this.saveRecords();
  },

  isTodayEligible: function (rec, date) {
    if (rec.repeatType.startsWith('daily')) return true;
    if (rec.repeatType === 'interval') {
      const start = new Date(new Date(rec.createdAt).setHours(0, 0, 0, 0));
      const target = new Date(new Date(date).setHours(0, 0, 0, 0));
      const days = Math.floor((target - start) / 86400000);
      return days >= 0 && (days % rec.intervalDays === 0);
    }
    if (rec.repeatType === 'weekly') {
      return rec.repeatWeekdays.includes(date.getDay());
    }
    return true;
  },

  getRepeatInfo: function (rec) {
    if (rec.repeatType === 'daily1') return '每天1次';
    if (rec.repeatType === 'daily2') return '每天2次';
    if (rec.repeatType === 'daily3') return '每天3次';
    if (rec.repeatType === 'interval') return `间隔 ${rec.intervalDays} 天`;
    if (rec.repeatType === 'weekly') return `星期${rec.repeatWeekdays.map(d => ['日','一','二','三','四','五','六'][d]).join(',')}`;
    return '';
  },

  getPeriodInfo: function (rec) {
    return rec.timePeriods.map(p => {
      const map = { morning: '早上', noon: '中午', evening: '晚上' };
      const time = rec.periodTimes[p] || '--:--';
      return `${map[p]}(${time})`;
    }).join(', ');
  },

  getStatusInfo: function (rec) {
    const todayKey = new Date().toISOString().split('T')[0];
    return rec.timePeriods.map(p => {
      const map = { morning: '早上', noon: '中午', evening: '晚上' };
      const reminded = rec.remindedDates[p] === todayKey;
      return `${map[p]}:${reminded ? '已提醒' : '待提醒'}`;
    }).join(' | ');
  }
})