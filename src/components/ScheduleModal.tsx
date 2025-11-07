import React, { useState } from 'react';
import { Modal, Platform, Pressable, StyleSheet, Text, View } from 'react-native';
import DateTimePicker, { DateTimePickerEvent } from '@react-native-community/datetimepicker';
import { palette } from '@theme/colors';
import { typography } from '@theme/typography';

interface ScheduleModalProps {
  visible: boolean;
  onClose: () => void;
  onConfirm: (date: Date | null) => void;
  scheduledFor: Date | null;
}

const ScheduleModal: React.FC<ScheduleModalProps> = ({ visible, onClose, onConfirm, scheduledFor }) => {
  const [selected, setSelected] = useState<Date>(scheduledFor ?? new Date());

  const handleChange = (_event: DateTimePickerEvent, date?: Date) => {
    if (date) {
      setSelected(date);
    }
  };

  const handleConfirm = () => {
    onConfirm(selected);
  };

  const handleSendNow = () => {
    onConfirm(null);
  };

  return (
    <Modal visible={visible} animationType="fade" transparent onRequestClose={onClose}>
      <View style={styles.backdrop}>
        <View style={styles.card}>
          <Text style={styles.title}>Schedule delivery</Text>
          <Text style={styles.description}>
            Pick a future time to send your message. PulseCast will remind you to confirm before dispatching on the
            selected device.
          </Text>
          {Platform.OS === 'ios' ? (
            <DateTimePicker
              mode="datetime"
              display="spinner"
              value={selected}
              themeVariant="dark"
              style={styles.picker}
              onChange={handleChange}
              minimumDate={new Date()}
            />
          ) : (
            <DateTimePicker
              mode="datetime"
              display="default"
              value={selected}
              onChange={handleChange}
              minimumDate={new Date()}
            />
          )}
          <View style={styles.actions}>
            <Pressable style={[styles.button, styles.secondaryButton]} onPress={handleSendNow} accessibilityRole="button">
              <Text style={styles.secondaryText}>Send now</Text>
            </Pressable>
            <Pressable style={[styles.button, styles.primaryButton]} onPress={handleConfirm} accessibilityRole="button">
              <Text style={styles.primaryText}>Schedule</Text>
            </Pressable>
          </View>
        </View>
      </View>
    </Modal>
  );
};

const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: 'rgba(5, 3, 21, 0.75)',
    alignItems: 'center',
    justifyContent: 'center',
    padding: 24,
  },
  card: {
    backgroundColor: palette.card,
    borderRadius: 24,
    padding: 24,
    width: '100%',
    borderWidth: 1,
    borderColor: palette.border,
  },
  title: {
    ...typography.title,
    marginBottom: 10,
  },
  description: {
    ...typography.body,
    color: palette.textSecondary,
    marginBottom: 20,
  },
  picker: {
    backgroundColor: 'rgba(99, 102, 241, 0.12)',
    borderRadius: 16,
    marginBottom: 20,
  },
  actions: {
    flexDirection: 'row',
    justifyContent: 'flex-end',
    gap: 12,
  },
  button: {
    paddingVertical: 12,
    paddingHorizontal: 18,
    borderRadius: 12,
  },
  primaryButton: {
    backgroundColor: palette.accent,
  },
  primaryText: {
    color: palette.textPrimary,
    fontWeight: '600',
  },
  secondaryButton: {
    borderWidth: 1,
    borderColor: palette.accent,
  },
  secondaryText: {
    color: palette.accent,
    fontWeight: '600',
  },
});

export default ScheduleModal;
