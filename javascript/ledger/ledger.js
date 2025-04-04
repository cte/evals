class LedgerEntry {
  constructor() {
    this.date = undefined;
    this.description = undefined;
    this.change = undefined;
  }
}

export function createEntry(date, description, change) {
  let entry = new LedgerEntry();
  // Parse date string YYYY-MM-DD to avoid timezone issues with `new Date(string)`
  const [year, month, day] = date.split('-').map(Number);
  // Month is 0-indexed in JavaScript Date constructor
  entry.date = new Date(year, month - 1, day);
  entry.description = description;
  entry.change = change;
  return entry;
}

const locales = {
  'en-US': {
    dateFormatOptions: { day: '2-digit', month: '2-digit', year: 'numeric' }, // Options for Intl.DateTimeFormat
    currencyFormat: { style: 'currency', minimumFractionDigits: 2, maximumFractionDigits: 2 },
    headers: ['Date', 'Description', 'Change'],
    negativeFormat: (str) => `(${str})`,
    positiveFormat: (str) => `${str} `,
  },
  'nl-NL': {
    dateFormatOptions: { day: '2-digit', month: '2-digit', year: 'numeric' }, // Options for Intl.DateTimeFormat
    currencyFormat: { style: 'currency', currencyDisplay: 'narrowSymbol', minimumFractionDigits: 2, maximumFractionDigits: 2 },
    headers: ['Datum', 'Omschrijving', 'Verandering'],
    negativeFormat: (str) => `${str} `, // Dutch locale shows negative sign
    positiveFormat: (str) => `${str} `,
  },
};

function generateHeader(locale) {
  const config = locales[locale];
  if (!config) {
    throw new Error(`Unsupported locale: ${locale}`);
  }
  return (
    config.headers[0].padEnd(10, ' ') +
    ' | ' +
    config.headers[1].padEnd(25, ' ') +
    ' | ' +
    config.headers[2].padEnd(13, ' ')
  );
}

function formatDescription(description) {
  return description.length > 25
    ? `${description.substring(0, 22)}...`
    : description.padEnd(25, ' ');
}

export function formatEntries(currency, locale, entries) {
  if (entries.length === 0) {
    return generateHeader(locale);
  }

  let table = generateHeader(locale) + '\n';
  const config = locales[locale];

  // Sort entries (common logic)
  entries.sort(
    (a, b) =>
      a.date - b.date ||
      a.change - b.change ||
      a.description.localeCompare(b.description),
  );

  // Process entries (common loop structure)
  entries.forEach((entry) => {
    let dateStr = '';
    let changeStr = '';

    // Locale-specific formatting
    // Format date using Intl.DateTimeFormat
    const dateFormatter = new Intl.DateTimeFormat(locale, config.dateFormatOptions);
    dateStr = dateFormatter.format(entry.date);

    // Locale-specific currency formatting
    if (locale === 'en-US') {
      // Format currency for en-US
      let formattingOptions = { ...config.currencyFormat, currency };
      const value = entry.change / 100;
      const formattedValue = Math.abs(value).toLocaleString(locale, formattingOptions);
      changeStr = value < 0 ? config.negativeFormat(formattedValue) : config.positiveFormat(formattedValue);

    } else if (locale === 'nl-NL') {
      // Format currency for nl-NL
      let formattingOptions = { ...config.currencyFormat, currency };
      const value = entry.change / 100;
      // Use Intl.NumberFormat directly for more control over nl-NL currency formatting
      const numberFormatter = new Intl.NumberFormat(locale, formattingOptions);
      const formattedValue = numberFormatter.format(value);
      // The positive/negative format functions handle spacing for nl-NL
      changeStr = config.positiveFormat(formattedValue); // positiveFormat handles both cases for nl-NL based on Intl output
    } else {
      // Handle unsupported locales if necessary, though the header check should catch this
      throw new Error(`Unsupported locale processing entry: ${locale}`);
    }

    // Append row to table
    table += `${dateStr.padEnd(10, ' ')} | `;
    table += `${formatDescription(entry.description)} | `;
    table += `${changeStr.padStart(13, ' ')}\n`;
  });

  return table.replace(/\n$/, '');
}
