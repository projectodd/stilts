
module Stilts
  module Stomp 


    class Client < org.projectodd.stilts.stomp.client::StompClient

      alias_method :original_subscribe, :subscribe

      def subscribe(destination, opts={}, &block) 
        builder = original_subscribe( destination )
        builder.withSelector( opts[:selector] ) if ( opts[:selector] ) 
        builder.withMessageHandler( BlockMessageHandler.new( block ) )
        builder.withAckMode( org.projectodd.stilts.stomp::Subscription::AckMode::AUTO )
        builder.start
      end

      alias_method :original_send, :send
    
      def send(destination, message)
        stomp_message = org.projectodd.stilts.stomp::StompMessages.createStompMessage( destination, message )
        original_send( stomp_message )
      end

    end # -- Client

    class BlockMessageHandler

      include org.projectodd.stilts.stomp.client::MessageHandler

      def initialize(block)
        @block = block
      end

      def handle(message)
        @block.call( StompMessage.new( message ) )
      end

    end # -- BlockMessageHandler

    class StompMessage
      def initialize(message)
        @message = message
      end

      def body
        @message.getContentAsString()
      end

    end

  end
end
