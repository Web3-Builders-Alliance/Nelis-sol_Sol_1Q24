package spl.cards.app.ui.screen

import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.os.Bundle

fun hasNfcAdapter(context: Context): Boolean = (context.getSystemService(Context.NFC_SERVICE) as NfcManager).defaultAdapter != null

fun initNfcAdapter(context: Context): NfcAdapter = (context.getSystemService(Context.NFC_SERVICE) as NfcManager).defaultAdapter

fun enableReaderMode(activity: Activity, nfcAdapter: NfcAdapter?, onReadTag: (tag: Tag) -> Unit) {
    // Work around for some broken Nfc firmware implementations that poll the card too fast
    val options = Bundle()
    options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)

    nfcAdapter?.enableReaderMode(
        activity, { tag: Tag ->
            onReadTag(tag)
        }, NfcAdapter.FLAG_READER_NFC_A or
                NfcAdapter.FLAG_READER_NFC_B or
                NfcAdapter.FLAG_READER_NFC_F or
                NfcAdapter.FLAG_READER_NFC_V or
                NfcAdapter.FLAG_READER_NFC_BARCODE or
                NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, options
    )
}
