import React from 'react';
import { StyleSheet, Text, View } from 'react-native';
import { palette } from '@theme/colors';
import { typography } from '@theme/typography';
import { estimateSmsSegments } from '@utils/validation';

interface MessagePreviewCardProps {
  message: string;
  recipientCount: number;
}

const MessagePreviewCard: React.FC<MessagePreviewCardProps> = ({ message, recipientCount }) => {
  const segments = estimateSmsSegments(message);
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Preview</Text>
      <View style={styles.previewBubble}>
        <Text style={styles.previewText}>{message || 'Message content will appear here.'}</Text>
      </View>
      <View style={styles.metaRow}>
        <Text style={styles.metaText}>{message.length} characters</Text>
        <Text style={styles.metaText}>~{segments || 1} segment{segments === 1 ? '' : 's'}</Text>
        <Text style={styles.metaText}>{recipientCount} recipients</Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    backgroundColor: palette.card,
    borderRadius: 24,
    padding: 20,
    marginBottom: 24,
    borderWidth: 1,
    borderColor: 'rgba(236, 72, 153, 0.2)',
  },
  title: {
    ...typography.title,
    marginBottom: 14,
  },
  previewBubble: {
    backgroundColor: 'rgba(99, 102, 241, 0.15)',
    borderRadius: 18,
    padding: 16,
    marginBottom: 16,
  },
  previewText: {
    ...typography.body,
    color: palette.textPrimary,
    lineHeight: 22,
  },
  metaRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  metaText: {
    ...typography.caption,
  },
});

export default MessagePreviewCard;
