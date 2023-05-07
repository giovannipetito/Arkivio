package it.giovanni.arkivio.fragments.detail.puntonet.coroutines

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CoroutineChannelsViewModel : ViewModel() {

    // The channel1 variable will be able to send and receive the language.
    private var channel1 = Channel<Language>()

    // The channel2 variable will close automatically by itself (we don't need to call channel2.close()).
    private var channel2: ReceiveChannel<Language> = Channel()

    private var channel3: ReceiveChannel<Language> = Channel()

    private var channel4: ReceiveChannel<Language> = Channel()

    private var channel5: ReceiveChannel<Language> = Channel()

    private var channel6: ReceiveChannel<Language> = Channel()

    init {

        /*
        // Coroutine #1: used as a Producer, to produce or send some data.
        viewModelScope.launch {
            channel1.send(Language.Kotlin)
            Log.i("[Coroutine]", "Kotlin sent!")
            channel1.send(Language.Java)
            Log.i("[Coroutine]", "Java sent!")
        }

        // Coroutine #2: used as a Receiver, to receive the data that we pass through the channel.
        viewModelScope.launch {
            // channel.isClosedForReceive returns true if this channel was closed by invocation of
            // close on the SendChannel side and all previously sent items were already received.
            Log.i("[Coroutine]", "channel1.isClosedForReceive: " + channel1.isClosedForReceive)

            // The receive() function will retrieve and remove an element from the channel if it's
            // not empty and will only receive one element from the channel and not more then that.
            Log.i("[Coroutine]", "channel1.receive(): " + channel1.receive())

            // So, in order to get also the Java constant, we need to call channel.receive() again:
            Log.i("[Coroutine]", "channel1.receive(): " + channel1.receive())

            channel1.close()

            // If this channel.isClosedForReceive prints false, it means that the channel was not
            // actually closed, even if we have received all the elements from the channel. Let's
            // add channel.close() before.
            Log.i("[Coroutine]", "channel1.isClosedForReceive: " + channel1.isClosedForReceive)
        }
        */

        /*
        viewModelScope.launch {
            // The produce function launches a new coroutine to produce a stream of values by sending
            // them to a channel and returns a reference to the coroutine as a ReceiveChannel. This
            // resulting object can be used to receive elements produced by this coroutine.
            // The channel is closed when the coroutine completes. The running coroutine is cancelled
            // when its receive channel is cancelled.
            channel2 = produce {
                send(Language.Python)
                send(Language.Javascript)
            }
        }

        viewModelScope.launch {
            Log.i("[Coroutine]", "channel2.isClosedForReceive: " + channel2.isClosedForReceive)

            // Performs the given action for each received element and cancels the channel after the
            // execution of the block.
            channel2.consumeEach {
                Log.i("[Coroutine]", it.name)
            }

            Log.i("[Coroutine]", "channel2.isClosedForReceive: " + channel2.isClosedForReceive)
        }
        */

        /**
         * Channel Types:
         *
         * - Buffered
         * - Conflated
         * - Rendezvous
         * - Unlimited
         */

        /**
         * Buffered
         * The Buffered channel allow us to specify the maximum capacity of the buffer, which means
         * that we are not able to send more data than the buffer capacity until we receive the data
         * and make some more space for the buffer to receive the new data. Because of this buffer
         * limit, if we send an element while the channel is full, then buffer will be suspended
         * until more space is freed up. The buffer can be freed up by calling the receive function.
         *
         * The capacity of the channel is 2, it means that we will not be able to send all the
         * elements (Kotlin, Java, Python, Javascript) at once. So first we will send only two
         * elements (Kotlin, Java) because the capacity is 2 and then we will receive that two
         * elements from our receiver coroutine and only after we received that elements we're
         * going to remove that elements from our actual channel buffer, and then we are going
         * to free one more space to send a new element (Python) to our channel. And after that
         * we free that actual one space from our channel, we can send one more (because the
         * capacity is 2) and after that we receive one more element from our channel, then we
         * will remove that element from our channel and we will make a new space for our new
         * element (Javascript) to be sent through the channel.
         *
         * After that the first two elements (Kotlin, Java) are sent, the coroutine is suspended
         * (because the capacity is 2) until we receive the first of the two elements from the
         * channel and we free some space from the buffer, after that a new element can be sent
         * to our channel and our channel is once again suspended until we receive one element
         * again so we can free some space inside our channel.
         */

        /*
        viewModelScope.launch {
            // Let's limit the Buffer capacity of channel3:
            channel3 = produce(capacity = 2) {
                send(Language.Kotlin)
                Log.i("[Coroutine]", "Kotlin sent!")
                send(Language.Java)
                Log.i("[Coroutine]", "Java sent!")
                send(Language.Python)
                Log.i("[Coroutine]", "Python sent!")
                send(Language.Javascript)
                Log.i("[Coroutine]", "Javascript sent!")
            }
        }

        viewModelScope.launch {
            Log.i("[Coroutine]", "channel3.receive(): " + channel3.receive())
            delay(3000L)
            Log.i("[Coroutine]", "------------------------------")
            Log.i("[Coroutine]", "channel3.receive(): " + channel3.receive())
            delay(3000L)
            Log.i("[Coroutine]", "------------------------------")
            Log.i("[Coroutine]", "channel3.receive(): " + channel3.receive())
            delay(3000L)
            Log.i("[Coroutine]", "------------------------------")
            Log.i("[Coroutine]", "channel3.receive(): " + channel3.receive())
            delay(3000L)
            Log.i("[Coroutine]", "------------------------------")
        }
        */

        /**
         * Conflated
         * The Conflated channel is a type of channel which has a capacity limit of number 1.
         * With a Conflated type, all new elements will replace the previous ones, so we will be
         * able to receive only the last one, thererfore we will lose all other previous elements
         * sent to the channel.
         */

        /*
        viewModelScope.launch {
            channel4 = produce(capacity = CONFLATED) {
                send(Language.Kotlin)
                Log.i("[Coroutine]", "Kotlin sent!")
                send(Language.Java)
                Log.i("[Coroutine]", "Java sent!")
                send(Language.Python)
                Log.i("[Coroutine]", "Python sent!")
                send(Language.Javascript)
                Log.i("[Coroutine]", "Javascript sent!")
            }
        }

        viewModelScope.launch {
            channel4.consumeEach {
                Log.i("[Coroutine]", it.name) // Only the last element, Javascript, will be printed.
            }
        }
        */

        /**
         * Rendezvous
         * Rendezvous is a default channel type, so we don't need to specify the type in the produce
         * function (by fedault each channel has a Rendezvous type). The Rendezvous channel is a
         * buffer channel with a capacity of 0, which means that the send operation will get suspended
         * until we actually receive that element that we have sent.
         */

        /*
        viewModelScope.launch {
            channel5 = produce {
                send(Language.Kotlin)
                Log.i("[Coroutine]", "Kotlin sent!")
                send(Language.Java)
                Log.i("[Coroutine]", "Java sent!")
                send(Language.Python)
                Log.i("[Coroutine]", "Python sent!")
                send(Language.Javascript)
                Log.i("[Coroutine]", "Javascript sent!")
            }
        }

        viewModelScope.launch {
            Log.i("[Coroutine]", "channel5.receive(): " + channel5.receive())
            delay(3000L)
            Log.i("[Coroutine]", "channel5.receive(): " + channel5.receive())

            // channel5.receive() will resume our channel (actually suspended) in order to receive
            // the second element. We should call channel5.receive() two more times in order to
            // receive all the four elements.
        }
        */

        /**
         * Unlimited
         * The capacity of our channel (or our buffer) will be unlimited, we can send as many
         * elements to this channel as we want. However, even though the unlimited channels have an
         * unlimited buffer size, for example if no memory is available and you try to send more
         * elements to it, you can eventually get a MemoryException.
         */

        viewModelScope.launch {
            channel6 = produce(capacity = UNLIMITED) {
                send(Language.Kotlin)
                Log.i("[Coroutine]", "Kotlin sent!")
                send(Language.Java)
                Log.i("[Coroutine]", "Java sent!")
                send(Language.Python)
                Log.i("[Coroutine]", "Python sent!")
                send(Language.Javascript)
                Log.i("[Coroutine]", "Javascript sent!")
            }
        }

        viewModelScope.launch {
            Log.i("[Coroutine]", "channel6.receive(): " + channel6.receive())
            delay(3000L)
            Log.i("[Coroutine]", "channel6.receive(): " + channel6.receive())
        }

        // We can send all the elements without waiting for one of those elements to be received.
    }
}

// The Enum will be used to send and receive the data from the channel.
enum class Language {
    Kotlin,
    Java,
    Python,
    Javascript
}