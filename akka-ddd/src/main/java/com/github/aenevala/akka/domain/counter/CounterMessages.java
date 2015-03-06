package com.github.aenevala.akka.domain.counter;

import java.io.Serializable;

import com.github.aenevala.akka.domain.EntityMessage;

public abstract class CounterMessages {
    public static class Increase extends EntityMessage implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = -4121922024514233771L;

        public Increase(String id) {
            super(id);
        }

    }

    public static class Decrease extends EntityMessage implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 255131936506429690L;

        public Decrease(String id) {
            super(id);
        }

    }

    public static class Get extends EntityMessage implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -7869300410793718633L;

        public Get(String id) {
            super(id);
        }

    }

    public static class GetReply extends EntityMessage implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        private final int count;

        public GetReply(String id, int count) {
            super(id);
            this.count = count;
        }

        public int getCount() {
            return count;
        }

    }

    public static class CountChanged extends EntityMessage implements Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 726215547535187405L;
        private final int delta;

        public CountChanged(String id, int delta) {
            super(id);
            this.delta = delta;
        }

        public int getCount() {
            return delta;
        }

    }
}
