import React, { useMemo, useState } from 'react';
import {
  ActivityIndicator,
  Alert,
  Platform,
  Pressable,
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  View,
} from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import * as SMS from 'expo-sms';
import { palette } from '@theme/colors';
import { typography } from '@theme/typography';
import RecipientManager from '@components/RecipientManager';
import MessagePreviewCard from '@components/MessagePreviewCard';
import ScheduleModal from '@components/ScheduleModal';
import { useRecipients } from '@hooks/useRecipients';
import { useTemplates } from '@hooks/useTemplates';

const formatSchedule = (date: Date) =>
  Intl.DateTimeFormat(undefined, { dateStyle: 'medium', timeStyle: 'short' }).format(date);

const ComposeScreen: React.FC = () => {
  const { recipients, addRecipient, importBulk, removeRecipient, clear, totalInvalid } = useRecipients();
  const { templates } = useTemplates();
  const [selectedTemplateId, setSelectedTemplateId] = useState<string | null>(templates[0]?.id ?? null);
  const [message, setMessage] = useState<string>(templates[0]?.body ?? '');
  const [statusMessage, setStatusMessage] = useState<string | null>(null);
  const [sending, setSending] = useState<boolean>(false);
  const [scheduleVisible, setScheduleVisible] = useState(false);
  const [scheduledFor, setScheduledFor] = useState<Date | null>(null);

  const canSend = useMemo(() => recipients.length > 0 && message.trim().length > 0, [recipients.length, message]);

  const handleTemplateApply = (templateId: string) => {
    const template = templates.find((item) => item.id === templateId);
    if (template) {
      setSelectedTemplateId(templateId);
      setMessage(template.body);
    }
  };

  const handleSend = async () => {
    if (!canSend) {
      setStatusMessage('Add at least one recipient and compose a message.');
      return;
    }

    if (scheduledFor && scheduledFor > new Date()) {
      setStatusMessage(`Scheduled for ${formatSchedule(scheduledFor)}. Open the app then to confirm.`);
      Alert.alert('Message scheduled', 'We will remind you to send this message at the scheduled time.');
      return;
    }

    try {
      setSending(true);
      const isAvailable = await SMS.isAvailableAsync();
      if (!isAvailable) {
        setStatusMessage('SMS service is not available on this device.');
        Alert.alert('SMS unavailable', 'This device cannot send SMS messages.');
        return;
      }
      const phones = recipients.map((recipient) => recipient.phone);
      const result = await SMS.sendSMSAsync(phones, message.trim());
      if (result.result === 'sent' || result.result === 'queued') {
        setStatusMessage('Message handed off to the Android SMS composer.');
        clear();
        setScheduledFor(null);
      } else {
        setStatusMessage('Message was not sent. Review the composer for details.');
      }
    } catch (error) {
      console.error('Failed to send SMS', error);
      setStatusMessage('Unexpected error while trying to send your SMS.');
    } finally {
      setSending(false);
    }
  };

  const renderTemplates = () => (
    <View style={styles.templateContainer}>
      <Text style={styles.sectionHeading}>Templates</Text>
      <ScrollView horizontal showsHorizontalScrollIndicator={false} style={styles.templateScroll}>
        {templates.map((template) => {
          const isActive = template.id === selectedTemplateId;
          return (
            <Pressable
              key={template.id}
              style={[styles.templateCard, isActive ? styles.templateCardActive : undefined]}
              onPress={() => handleTemplateApply(template.id)}
              accessibilityRole="button"
            >
              <Text style={styles.templateTitle}>{template.name}</Text>
              <Text style={styles.templateBody} numberOfLines={3}>
                {template.body}
              </Text>
            </Pressable>
          );
        })}
      </ScrollView>
    </View>
  );

  return (
    <LinearGradient colors={[palette.background, '#120C3D']} style={styles.screen}>
      <ScrollView contentContainerStyle={styles.content}>
        <View style={styles.hero}>
          <Text style={styles.headline}>PulseCast Broadcasts</Text>
          <Text style={styles.subtitle}>
            Send beautifully crafted updates to your community in seconds. Import contacts, personalize your copy, and
            launch.
          </Text>
        </View>

        <RecipientManager
          recipients={recipients}
          totalInvalid={totalInvalid}
          onAddRecipient={addRecipient}
          onImport={importBulk}
          onRemoveRecipient={removeRecipient}
        />

        <View style={styles.composerCard}>
          <Text style={styles.sectionHeading}>Message</Text>
          <TextInput
            value={message}
            onChangeText={setMessage}
            placeholder="Draft your announcement..."
            placeholderTextColor={palette.textMuted}
            multiline
            numberOfLines={6}
            style={styles.messageInput}
          />
          <View style={styles.toolbar}>
            <Pressable style={styles.toolbarButton} onPress={() => setScheduleVisible(true)} accessibilityRole="button">
              <Text style={styles.toolbarButtonText}>
                {scheduledFor ? `Scheduled: ${formatSchedule(scheduledFor)}` : 'Schedule delivery'}
              </Text>
            </Pressable>
            <Pressable style={[styles.toolbarButton, styles.clearButton]} onPress={clear} accessibilityRole="button">
              <Text style={[styles.toolbarButtonText, styles.clearButtonText]}>Clear recipients</Text>
            </Pressable>
          </View>
        </View>

        {renderTemplates()}

        <MessagePreviewCard message={message} recipientCount={recipients.length} />

        <Pressable
          style={[styles.sendButton, !canSend ? styles.sendButtonDisabled : undefined]}
          onPress={handleSend}
          disabled={!canSend || sending}
          accessibilityRole="button"
        >
          {sending ? <ActivityIndicator color={palette.textPrimary} /> : <Text style={styles.sendButtonText}>Send SMS</Text>}
        </Pressable>

        {statusMessage ? <Text style={styles.statusMessage}>{statusMessage}</Text> : null}

        <View style={styles.disclaimer}>
          <Text style={styles.disclaimerTitle}>Production ready guidance</Text>
          <Text style={styles.disclaimerText}>
            Ensure the Android build has the SEND_SMS permission granted. Devices on Android 4.4+ will open the default
            messaging app composer for confirmation before dispatching messages. For fully automated sending, integrate
            with an approved SMS gateway service.
          </Text>
          {Platform.OS === 'android' ? null : (
            <Text style={[styles.disclaimerText, styles.disclaimerHint]}>
              SMS is previewed here. Sending requires running the app on an Android device or emulator.
            </Text>
          )}
        </View>
      </ScrollView>

      <ScheduleModal
        visible={scheduleVisible}
        onClose={() => setScheduleVisible(false)}
        scheduledFor={scheduledFor}
        onConfirm={(date) => {
          setScheduleVisible(false);
          setScheduledFor(date);
          setStatusMessage(date ? `Scheduled for ${formatSchedule(date)}.` : 'Dispatch will happen immediately.');
        }}
      />
    </LinearGradient>
  );
};

const styles = StyleSheet.create({
  screen: {
    flex: 1,
  },
  content: {
    padding: 24,
  },
  hero: {
    marginBottom: 24,
  },
  headline: {
    ...typography.display,
    marginBottom: 12,
  },
  subtitle: {
    ...typography.body,
    color: palette.textSecondary,
    lineHeight: 22,
  },
  composerCard: {
    backgroundColor: palette.card,
    borderRadius: 24,
    padding: 20,
    marginBottom: 24,
    borderWidth: 1,
    borderColor: palette.border,
  },
  sectionHeading: {
    ...typography.title,
    marginBottom: 16,
  },
  messageInput: {
    minHeight: 160,
    backgroundColor: 'rgba(5, 3, 21, 0.4)',
    borderRadius: 18,
    padding: 16,
    color: palette.textPrimary,
    textAlignVertical: 'top',
    borderWidth: 1,
    borderColor: palette.border,
  },
  toolbar: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: 16,
    gap: 12,
  },
  toolbarButton: {
    flex: 1,
    paddingVertical: 12,
    borderRadius: 14,
    borderWidth: 1,
    borderColor: palette.accent,
    alignItems: 'center',
    justifyContent: 'center',
  },
  toolbarButtonText: {
    color: palette.accent,
    fontWeight: '600',
  },
  clearButton: {
    borderColor: 'rgba(248, 113, 113, 0.4)',
  },
  clearButtonText: {
    color: palette.error,
  },
  templateContainer: {
    marginBottom: 24,
  },
  templateScroll: {
    flexGrow: 0,
  },
  templateCard: {
    backgroundColor: palette.card,
    borderRadius: 20,
    padding: 18,
    width: 220,
    marginRight: 16,
    borderWidth: 1,
    borderColor: 'rgba(99, 102, 241, 0.2)',
  },
  templateCardActive: {
    borderColor: palette.accent,
    shadowColor: palette.accent,
    shadowOpacity: 0.3,
    shadowRadius: 8,
    shadowOffset: { width: 0, height: 6 },
    elevation: 6,
  },
  templateTitle: {
    ...typography.body,
    fontWeight: '600',
    marginBottom: 10,
    color: palette.textPrimary,
  },
  templateBody: {
    ...typography.caption,
    color: palette.textSecondary,
    lineHeight: 18,
  },
  sendButton: {
    backgroundColor: palette.accentSecondary,
    paddingVertical: 16,
    borderRadius: 20,
    alignItems: 'center',
    marginBottom: 16,
  },
  sendButtonDisabled: {
    opacity: 0.5,
  },
  sendButtonText: {
    color: palette.textPrimary,
    fontSize: 18,
    fontWeight: '700',
    letterSpacing: 0.5,
  },
  statusMessage: {
    ...typography.caption,
    marginBottom: 16,
    color: palette.textSecondary,
  },
  disclaimer: {
    backgroundColor: 'rgba(20, 17, 63, 0.6)',
    padding: 18,
    borderRadius: 18,
    borderWidth: 1,
    borderColor: palette.border,
    marginBottom: 32,
  },
  disclaimerTitle: {
    ...typography.body,
    fontWeight: '600',
    marginBottom: 8,
    color: palette.textPrimary,
  },
  disclaimerText: {
    ...typography.caption,
    color: palette.textSecondary,
    lineHeight: 18,
  },
  disclaimerHint: {
    marginTop: 8,
  },
});

export default ComposeScreen;
