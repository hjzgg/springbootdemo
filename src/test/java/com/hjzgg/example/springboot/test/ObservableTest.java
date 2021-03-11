package com.hjzgg.example.springboot.test;

public class ObservableTest {
    public static void main(String[] args) {
        Observable.send()
                .lift(new Operator() {
                    @Override
                    public Subscriber call(Subscriber s) {
                        return new Subscriber() {
                            @Override
                            void onNext(Object o) {
                                s.onNext(o + " first lift……");
                            }
                        };
                    }
                })
                .lift(new Operator() {
                    @Override
                    public Subscriber call(Subscriber s) {
                        return new Subscriber() {
                            @Override
                            void onNext(Object o) {
                                s.onNext(o + " second lift");
                            }
                        };
                    }
                })
                .subscribe(new Subscriber() {
                    @Override
                    void onNext(Object o) {
                        System.out.println("result: " + o);
                    }
                });
    }

    static class Observable {
        private OnSubscribe onSubscribe;

        public Observable(OnSubscribe onSubscribe) {
            this.onSubscribe = onSubscribe;
        }

        public Observable lift(Operator operator) {
            return new Observable(new OnSubscribe() {
                @Override
                public void call(Subscriber s) {
                    onSubscribe.call(operator.call(s));
                }
            });
        }

        public void subscribe(Subscriber subscriber) {
            onSubscribe.call(subscriber);
        }

        public static Observable send() {
            return new Observable(
                    new OnSubscribe() {
                        @Override
                        public void call(Subscriber s) {
                            s.onNext(1);
                            s.onNext(2);
                        }
                    }
            );
        }
    }

    interface OnSubscribe {
        void call(Subscriber s);
    }

    interface Operator {
        Subscriber call(Subscriber s);
    }

    static abstract class Subscriber {
        abstract void onNext(Object o);
    }
}