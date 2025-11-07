import { useCallback, useMemo, useState } from 'react';
import { validatePhoneNumber } from '@utils/validation';

type RecipientOrigin = 'manual' | 'imported';

export interface Recipient {
  id: string;
  name: string;
  phone: string;
  origin: RecipientOrigin;
}

const createRecipient = (name: string, phone: string, origin: RecipientOrigin): Recipient => ({
  id: `${origin}-${phone}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
  name,
  phone,
  origin,
});

const sanitizePhone = (value: string): string => value.replace(/[^+\d]/g, '');

export interface UseRecipientsReturn {
  recipients: Recipient[];
  addRecipient: (input: { name?: string; phone: string }, origin?: RecipientOrigin) => void;
  importBulk: (csvLike: string) => { imported: number; skipped: number };
  removeRecipient: (id: string) => void;
  clear: () => void;
  totalValid: number;
  totalInvalid: number;
}

export const useRecipients = (): UseRecipientsReturn => {
  const [recipients, setRecipients] = useState<Recipient[]>([]);
  const [invalidPhones, setInvalidPhones] = useState<number>(0);

  const addRecipient = useCallback(
    ({ name, phone }: { name?: string; phone: string }, origin: RecipientOrigin = 'manual') => {
      const trimmedPhone = sanitizePhone(phone);
      const trimmedName = (name ?? '').trim();
      if (!trimmedPhone) {
        setInvalidPhones((prev) => prev + 1);
        return;
      }
      if (!validatePhoneNumber(trimmedPhone)) {
        setInvalidPhones((prev) => prev + 1);
        return;
      }
      setRecipients((prev) => {
        const isDuplicate = prev.some((recipient) => recipient.phone === trimmedPhone);
        if (isDuplicate) {
          return prev;
        }
        return [...prev, createRecipient(trimmedName || trimmedPhone, trimmedPhone, origin)];
      });
    },
    [],
  );

  const importBulk = useCallback(
    (csvLike: string) => {
      const rows = csvLike
        .split(/\n|,|;|\t/)
        .map((row) => row.trim())
        .filter(Boolean);
      let imported = 0;
      let skipped = 0;
      rows.forEach((row) => {
        const [maybeName, maybePhone] = row.split(/[:|]/).map((part) => part.trim());
        const candidatePhone = maybePhone ?? maybeName;
        const candidateName = maybePhone ? maybeName : undefined;
        if (!candidatePhone) {
          skipped += 1;
          return;
        }
        const sanitizedPhone = sanitizePhone(candidatePhone);
        if (!validatePhoneNumber(sanitizedPhone)) {
          skipped += 1;
          return;
        }
        setRecipients((prev) => {
          const exists = prev.some((recipient) => recipient.phone === sanitizedPhone);
          if (exists) {
            skipped += 1;
            return prev;
          }
          imported += 1;
          return [...prev, createRecipient(candidateName ?? sanitizedPhone, sanitizedPhone, 'imported')];
        });
      });
      return { imported, skipped };
    },
    [],
  );

  const removeRecipient = useCallback((id: string) => {
    setRecipients((prev) => prev.filter((recipient) => recipient.id !== id));
  }, []);

  const clear = useCallback(() => {
    setRecipients([]);
    setInvalidPhones(0);
  }, []);

  const stats = useMemo(
    () => ({
      totalValid: recipients.length,
      totalInvalid: invalidPhones,
    }),
    [recipients.length, invalidPhones],
  );

  return {
    recipients,
    addRecipient,
    importBulk,
    removeRecipient,
    clear,
    ...stats,
  };
};
