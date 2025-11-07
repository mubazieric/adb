import React, { useMemo, useState } from 'react';
import { KeyboardAvoidingView, Platform, Pressable, StyleSheet, Text, TextInput, View } from 'react-native';
import { LinearGradient } from 'expo-linear-gradient';
import { palette } from '@theme/colors';
import { typography } from '@theme/typography';
import RecipientChip from './RecipientChip';
import { Recipient } from '@hooks/useRecipients';

interface RecipientManagerProps {
  recipients: Recipient[];
  totalInvalid: number;
  onAddRecipient: (payload: { name?: string; phone: string }) => void;
  onImport: (payload: string) => { imported: number; skipped: number };
  onRemoveRecipient: (id: string) => void;
}

const RecipientManager: React.FC<RecipientManagerProps> = ({
  recipients,
  totalInvalid,
  onAddRecipient,
  onImport,
  onRemoveRecipient,
}) => {
  const [name, setName] = useState('');
  const [phone, setPhone] = useState('');
  const [bulk, setBulk] = useState('');
  const [importStats, setImportStats] = useState<{ imported: number; skipped: number } | null>(null);

  const hasRecipients = recipients.length > 0;
  const summaryText = useMemo(() => {
    if (!hasRecipients) {
      return 'Add recipients manually or paste numbers to import in bulk.';
    }
    return `${recipients.length} recipients ready • ${totalInvalid} invalid attempts filtered`;
  }, [hasRecipients, recipients.length, totalInvalid]);

  const handleAdd = () => {
    onAddRecipient({ name: name.trim(), phone: phone.trim() });
    setPhone('');
    setName('');
  };

  const handleBulkImport = () => {
    const stats = onImport(bulk);
    setImportStats(stats);
    setBulk('');
  };

  return (
    <KeyboardAvoidingView behavior={Platform.select({ ios: 'padding', android: undefined })}>
      <LinearGradient colors={[palette.cardHighlight, palette.card]} style={styles.container}>
        <View style={styles.header}>
          <Text style={typography.title}>Recipients</Text>
          <Text style={[typography.caption, styles.caption]}>{summaryText}</Text>
        </View>
        <View style={styles.row}>
          <View style={styles.fieldContainer}>
            <Text style={styles.label}>Name (optional)</Text>
            <TextInput
              value={name}
              onChangeText={setName}
              placeholder="Customer name"
              placeholderTextColor={palette.textMuted}
              style={styles.input}
              returnKeyType="next"
            />
          </View>
          <View style={styles.fieldContainer}>
            <Text style={styles.label}>Phone Number</Text>
            <TextInput
              value={phone}
              onChangeText={setPhone}
              placeholder="e.g. +15551234567"
              placeholderTextColor={palette.textMuted}
              keyboardType="phone-pad"
              style={styles.input}
              returnKeyType="done"
              onSubmitEditing={handleAdd}
            />
          </View>
        </View>
        <Pressable style={styles.primaryButton} onPress={handleAdd} accessibilityRole="button">
          <Text style={styles.primaryButtonText}>Add Recipient</Text>
        </Pressable>

        <View style={styles.bulkContainer}>
          <Text style={styles.label}>Bulk Import</Text>
          <TextInput
            value={bulk}
            onChangeText={setBulk}
            placeholder="Paste comma, newline, or tab separated numbers"
            placeholderTextColor={palette.textMuted}
            multiline
            numberOfLines={3}
            style={[styles.input, styles.bulkInput]}
          />
          <Pressable style={styles.secondaryButton} onPress={handleBulkImport} accessibilityRole="button">
            <Text style={styles.secondaryButtonText}>Import Recipients</Text>
          </Pressable>
          {importStats ? (
            <Text style={[typography.caption, styles.importStats]}>
              Imported {importStats.imported} • Skipped {importStats.skipped}
            </Text>
          ) : null}
        </View>

        {hasRecipients ? (
          <View style={styles.recipientsWrap}>
            {recipients.map((recipient) => (
              <RecipientChip key={recipient.id} recipient={recipient} onRemove={onRemoveRecipient} />
            ))}
          </View>
        ) : (
          <View style={styles.emptyState}>
            <Text style={styles.emptyStateText}>No recipients yet.</Text>
          </View>
        )}
      </LinearGradient>
    </KeyboardAvoidingView>
  );
};

const styles = StyleSheet.create({
  container: {
    borderRadius: 24,
    padding: 20,
    marginBottom: 24,
  },
  header: {
    marginBottom: 16,
  },
  caption: {
    marginTop: 4,
  },
  row: {
    flexDirection: 'row',
    gap: 12,
    marginBottom: 12,
  },
  fieldContainer: {
    flex: 1,
  },
  label: {
    ...typography.caption,
    marginBottom: 6,
    textTransform: 'uppercase',
    letterSpacing: 1,
  },
  input: {
    backgroundColor: 'rgba(5, 3, 21, 0.4)',
    borderWidth: 1,
    borderColor: palette.border,
    borderRadius: 14,
    paddingHorizontal: 14,
    paddingVertical: 12,
    color: palette.textPrimary,
    fontSize: 16,
  },
  primaryButton: {
    backgroundColor: palette.accent,
    paddingVertical: 12,
    borderRadius: 14,
    alignItems: 'center',
    marginBottom: 20,
  },
  primaryButtonText: {
    fontSize: 16,
    fontWeight: '600',
    color: palette.textPrimary,
  },
  bulkContainer: {
    marginBottom: 16,
  },
  bulkInput: {
    minHeight: 80,
    textAlignVertical: 'top',
  },
  secondaryButton: {
    marginTop: 12,
    paddingVertical: 10,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: palette.accent,
    alignItems: 'center',
  },
  secondaryButtonText: {
    color: palette.accent,
    fontWeight: '600',
  },
  importStats: {
    marginTop: 8,
  },
  recipientsWrap: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginTop: 12,
  },
  emptyState: {
    paddingVertical: 16,
    alignItems: 'center',
  },
  emptyStateText: {
    ...typography.body,
    color: palette.textMuted,
  },
});

export default RecipientManager;
