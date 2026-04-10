Page({
  data: {
    med: {
      id: '',
      name: '',
      dosage: '',
      beforeMeal: true,
      symptoms: '',
      notes: '',
      frequency: 1,
      slots: {
        morning: { time: '08:00' },
        noon: { time: '12:00' },
        evening: { time: '18:00' }
      },
      reminderType: 'popup',
      tracking: true
    },
    mealOptions: ['饭前', '饭后'],
    frequencyOptions: ['每天1次', '每天2次', '每天3次'],
    slotOptions: [
      { value: 'morning', label: '早上', start: '05:00', end: '09:00' },
      { value: 'noon', label: '中午', start: '09:00', end: '14:00' },
      { value: 'evening', label: '晚上', start: '14:00', end: '22:00' }
    ],
    reminderOptions: ['弹窗提醒', '铃声提醒'],
    selectedSlot: 'morning',
    selectedSlots: ['morning'],
    isEdit: false
  },

  onLoad: function(options) {
    if (options.id) {
      this.setData({ isEdit: true });
      this.loadMed(options.id);
    }
  },

  loadMed: function(id) {
    const medications = wx.getStorageSync('medications') || [];
    const med = medications.find(m => m.id === id);
    if (med) {
      const selectedSlots = this.getDefaultSlotsByFrequency(med.frequency, med.slots);
      const selectedSlot = selectedSlots[0] || 'morning';
      this.setData({ med: med, selectedSlot, selectedSlots });
    }
  },

  getDefaultSlotsByFrequency(frequency, slots) {
    if (frequency === 1) {
      const slot = ['morning', 'noon', 'evening'].find(k => slots[k] && slots[k].time) || 'morning';
      return [slot];
    }
    if (frequency === 2) {
      const filled = ['morning', 'noon', 'evening'].filter(k => slots[k] && slots[k].time);
      return filled.length === 2 ? filled : ['morning', 'evening'];
    }
    return ['morning', 'noon', 'evening'];
  },

  onNameInput: function(e) {
    this.setData({ 'med.name': e.detail.value });
  },

  onDosageInput: function(e) {
    this.setData({ 'med.dosage': e.detail.value });
  },

  onMealChange: function(e) {
    this.setData({ 'med.beforeMeal': e.detail.value === 0 });
  },

  onSymptomsInput: function(e) {
    this.setData({ 'med.symptoms': e.detail.value });
  },

  onNotesInput: function(e) {
    this.setData({ 'med.notes': e.detail.value });
  },

  onFrequencyChange: function(e) {
    const frequency = e.detail.value + 1;
    let selectedSlots = [];
    let selectedSlot = '';
    if (frequency === 1) {
      selectedSlots = ['morning'];
      selectedSlot = 'morning';
    } else if (frequency === 2) {
      selectedSlots = ['morning', 'evening'];
      selectedSlot = '';
    } else if (frequency === 3) {
      selectedSlots = ['morning', 'noon', 'evening'];
      selectedSlot = '';
    }

    this.setData({ 'med.frequency': frequency, selectedSlots, selectedSlot });
  },

  onSlotSelect: function(e) {
    const slot = e.detail.value;
    this.setData({ selectedSlot: slot, selectedSlots: [slot], 'med.frequency': 1 });
  },

  validateSlotTime(slot, time) {
    if (!time) return false;
    const slotInfo = this.data.slotOptions.find(item => item.value === slot);
    if (!slotInfo) return false;

    const toMin = t => {
      const [h, m] = t.split(':').map(Number);
      return h * 60 + m;
    };

    const t = toMin(time);
    if (t < toMin(slotInfo.start) || t > toMin(slotInfo.end)) {
      wx.showToast({ title: `${slotInfo.label}时间应在${slotInfo.start}-${slotInfo.end}之间`, icon: 'none' });
      return false;
    }
    return true;
  },

  onMorningTimeChange: function(e) {
    const value = e.detail.value;
    if (!this.validateSlotTime('morning', value)) {
      return;
    }
    this.setData({ 'med.slots.morning.time': value });
  },

  onNoonTimeChange: function(e) {
    const value = e.detail.value;
    if (!this.validateSlotTime('noon', value)) {
      return;
    }
    this.setData({ 'med.slots.noon.time': value });
  },

  onEveningTimeChange: function(e) {
    const value = e.detail.value;
    if (!this.validateSlotTime('evening', value)) {
      return;
    }
    this.setData({ 'med.slots.evening.time': value });
  },

  onReminderChange: function(e) {
    this.setData({ 'med.reminderType': e.detail.value === 0 ? 'popup' : 'ring' });
  },

  onTrackingChange: function(e) {
    this.setData({ 'med.tracking': e.detail.value });
  },

  onSave: function() {
    const med = this.data.med;
    if (!med.name || !med.dosage) {
      wx.showToast({ title: '请填写药品名称和剂量', icon: 'none' });
      return;
    }

    const frequency = med.frequency;

    if (frequency === 1) {
      if (!this.data.selectedSlot) {
        wx.showToast({ title: '请选择一个用药时段', icon: 'none' });
        return;
      }
      if (!this.validateSlotTime(this.data.selectedSlot, med.slots[this.data.selectedSlot].time)) {
        return;
      }
      // 其他时段禁用
      ['morning', 'noon', 'evening'].forEach(slot => {
        if (slot !== this.data.selectedSlot) med.slots[slot].time = '';
      });
    } else if (frequency === 2) {
      // 固定早晚，不允许中午
      const requiredSlots = ['morning', 'evening'];
      for (const slot of requiredSlots) {
        if (!this.validateSlotTime(slot, med.slots[slot].time)) {
          return;
        }
      }
      med.slots.noon.time = '';
      this.setData({ selectedSlots: ['morning', 'evening'] });
    } else if (frequency === 3) {
      ['morning', 'noon', 'evening'].forEach(slot => {
        if (!this.validateSlotTime(slot, med.slots[slot].time)) {
          return;
        }
      });
      this.setData({ selectedSlots: ['morning', 'noon', 'evening'] });
    }

    let medications = wx.getStorageSync('medications') || [];
    if (this.data.isEdit) {
      medications = medications.map(item => item.id === med.id ? med : item);
    } else {
      med.id = Date.now().toString();
      medications.push(med);
    }

    wx.setStorageSync('medications', medications);
    wx.showToast({ title: '保存成功' });
    wx.navigateBack();
  }
});