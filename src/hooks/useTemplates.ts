import { useMemo, useState } from 'react';

export interface MessageTemplate {
  id: string;
  name: string;
  body: string;
}

const DEFAULT_TEMPLATES: MessageTemplate[] = [
  {
    id: 'launch-update',
    name: 'Product Launch Update',
    body: 'Hi there! We are excited to share our latest product update with you. Tap the link to explore what is new.',
  },
  {
    id: 'event-reminder',
    name: 'Event Reminder',
    body: 'Friendly reminder about tomorrow’s event. Reply YES if you will join us or text HELP for details.',
  },
  {
    id: 'promo',
    name: 'Flash Promotion',
    body: 'Limited-time offer just for our insiders! Use code PULSE within 24 hours to unlock your reward.',
  },
];

export const useTemplates = () => {
  const [templates] = useState<MessageTemplate[]>(DEFAULT_TEMPLATES);
  const templateById = useMemo(() =>
    templates.reduce<Record<string, MessageTemplate>>((accumulator, template) => {
      accumulator[template.id] = template;
      return accumulator;
    }, {}), [templates]);

  return { templates, templateById };
};
