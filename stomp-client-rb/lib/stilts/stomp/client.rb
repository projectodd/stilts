
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
    
      def send(destination, message, headers = nil)

        method_args = [destination]

        if headers
          # convert Ruby hash to org.projectodd.stilts.stomp.Headers
          stomp_headers = org.projectodd.stilts.stomp.DefaultHeaders.new
          headers.each do |key,val|
            stomp_headers.put(key.to_s, val.to_s)
          end
          method_args << stomp_headers
        end

        method_args << message


        stomp_message = org.projectodd.stilts.stomp::StompMessages.createStompMessage( *method_args )

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
