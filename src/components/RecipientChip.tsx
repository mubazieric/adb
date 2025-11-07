import React from 'react';
import { Pressable, StyleSheet, Text, View } from 'react-native';
import { palette } from '@theme/colors';
import { typography } from '@theme/typography';
import { Recipient } from '@hooks/useRecipients';
import { MaterialCommunityIcons } from '@expo/vector-icons';

interface RecipientChipProps {
  recipient: Recipient;
  onRemove?: (id: string) => void;
}

const originBadgeColor: Record<Recipient['origin'], string> = {
  manual: palette.accent,
  imported: palette.accentSecondary,
};

const RecipientChip: React.FC<RecipientChipProps> = ({ recipient, onRemove }) => {
  return (
    <View style={styles.container}>
      <View style={[styles.badge, { backgroundColor: originBadgeColor[recipient.origin] }]} />
      <View style={styles.textContainer}>
        <Text style={styles.name}>{recipient.name}</Text>
        <Text style={styles.phone}>{recipient.phone}</Text>
      </View>
      {onRemove ? (
        <Pressable onPress={() => onRemove(recipient.id)} style={styles.removeButton} accessibilityRole="button">
          <MaterialCommunityIcons name="close" size={18} color={palette.textSecondary} />
        </Pressable>
      ) : null}
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingHorizontal: 14,
    paddingVertical: 10,
    backgroundColor: palette.chipBackground,
    borderRadius: 16,
    marginRight: 12,
    marginBottom: 12,
  },
  badge: {
    width: 6,
    height: 30,
    borderRadius: 3,
    marginRight: 12,
  },
  textContainer: {
    flex: 1,
  },
  name: {
    ...typography.body,
    color: palette.textPrimary,
    fontWeight: '600',
  },
  phone: {
    ...typography.caption,
    color: palette.textSecondary,
  },
  removeButton: {
    marginLeft: 8,
  },
});

export default RecipientChip;
